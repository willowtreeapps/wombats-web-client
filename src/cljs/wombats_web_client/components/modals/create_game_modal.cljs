(ns wombats-web-client.components.modals.create-game-modal
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [cljs-time.core :as time]
            [wombats-web-client.utils.forms
             :refer [submit-modal-input
                     cancel-modal-input]]
            [wombats-web-client.utils.games
             :refer [is-private?]]
            [wombats-web-client.components.text-input
             :refer [text-input-with-label]]
            [wombats-web-client.components.datepicker
             :refer [datepicker]]
            [wombats-web-client.components.radio-select
             :refer [radio-select]]
            [wombats-web-client.utils.errors :refer [get-error-message
                                                     required-field-error
                                                     not-an-integer
                                                     max-eight
                                                     incorrect-format-colon]]
            [wombats-web-client.events.games :refer [create-game]]))

(defonce radios ["public" "private"])
(defonce initial-cmpnt-state
  {:game-name nil
   :game-name-error nil
   :number-of-players nil
   :number-of-players-error nil
   :number-of-rounds nil
   :number-of-rounds-error nil
   :intermission-time "10:00"
   :intermission-time-error nil
   :round-time "2:00"
   :round-time-error nil
   :start-time nil
   :start-time-error nil
   :game-status (first radios)
   :password nil
   :password-error nil})

(defn input-error! [check cmpnt-state]
  (let [{:keys [key-name test-fn error]} check
        input (key-name @cmpnt-state)
        key-string (name key-name)
        error-key (keyword (str key-string "-error"))]

    (when (test-fn input)
      (swap! cmpnt-state assoc error-key error))))

(defn no-blanks? [value-list]
  ;; if something returned, a field is missing a value.
  (empty? (filter nil? value-list)))

(defn get-milliseconds [min-str]
  (let [list (clojure.string/split min-str #":")
        mins (js/parseInt (first list))
        secs (js/parseInt (last list))]
    (* 1000 (+ secs (* mins 60)))))

(defn get-utc [start-time]
  (let [date-split (clojure.string/split start-time #"-")
        year-num (js/parseInt (first date-split))
        month-num (js/parseInt (second date-split))
        time-string (clojure.string/split (last date-split) #"T")
        day-num (js/parseInt (first time-string))
        time-split (clojure.string/split (last time-string) #":")
        hour-num (js/parseInt (first time-split))
        min-num (js/parseInt (last time-split))
        local-time (time/local-date-time year-num
                                         month-num
                                         day-num
                                         hour-num
                                         min-num)
        utc-time-without-format (.toUTCIsoString
                                 (goog.date.UtcDateTime.fromTimestamp
                                  (.getTime local-time)) true)
        utc-split (clojure.string/split utc-time-without-format #" ")]
    (clojure.string/join "T" utc-split)))

(defn check-for-errors [cmpnt-state]
  (let [{:keys [:game-status
                :number-of-players
                :number-of-rounds]} @cmpnt-state
                validation-checks
                [{:key-name :game-name
                  :test-fn clojure.string/blank?
                  :error required-field-error}
                 {:key-name :number-of-players
                  :test-fn clojure.string/blank?
                  :error required-field-error}
                 {:key-name :number-of-players
                  :test-fn #(not
                             (integer? (js/parseInt %)))
                  :error not-an-integer}
                 {:key-name :number-of-players
                  :test-fn #(not
                             (< (js/parseInt %) 9))
                  :error max-eight}
                 {:key-name :number-of-rounds
                  :test-fn clojure.string/blank?
                  :error required-field-error}
                 {:key-name :number-of-rounds
                  :test-fn #(not
                             (integer? (js/parseInt %)))
                  :error not-an-integer}
                 {:key-name :intermission-time
                  :test-fn clojure.string/blank?
                  :error required-field-error}
                 {:key-name :intermission-time
                  :test-fn #(not
                             (clojure.string/includes? % ":"))
                  :error incorrect-format-colon}
                 {:key-name :start-time
                  :test-fn clojure.string/blank?
                  :error required-field-error}
                 {:key-name :round-time
                  :test-fn clojure.string/blank?
                  :error required-field-error}
                 {:key-name :round-time
                  :test-fn #(not
                             (clojure.string/includes? % ":"))
                  :error incorrect-format-colon}
                 {:key-name :password
                  :test-fn #(and
                             (is-private? game-status)
                             (clojure.string/blank? %))
                  :error required-field-error}]]

    (doall (map #(input-error! % cmpnt-state) validation-checks))))

(defn check-private-validity [game-status password]
  (if (is-private? game-status)
    (not (clojure.string/blank? password))
    true))

(def callback-error
  (fn [error cmpnt-state]
    (re-frame/dispatch [:update-modal-error (get-error-message error)])
    (reset! cmpnt-state initial-cmpnt-state)))

(defn on-submit-form-valid [arena-id cmpnt-state]
  (let [{:keys [game-name
                number-of-players
                number-of-rounds
                intermission-time
                start-time
                round-time
                game-status
                password
                password-error]} @cmpnt-state
                not-blank (no-blanks? [game-name
                                       number-of-players
                                       number-of-rounds
                                       intermission-time
                                       start-time])
                correct-private (check-private-validity
                                 game-status
                                 password)
                num-rounds (js/parseInt number-of-rounds)
                num-players (js/parseInt number-of-players)
                valid-answers (and (integer? num-rounds)
                                   (integer? num-players)
                                   (< num-players 9)
                                   correct-private)
                ready-to-submit (and not-blank valid-answers)]

    (check-for-errors cmpnt-state)

    (when ready-to-submit
      (let [intermission-ms (get-milliseconds intermission-time)
            round-ms (get-milliseconds round-time)
            utc-time (get-utc start-time)
            game-type :high-score ;; hardcoded for now
            is-private (is-private? game-status)
            password (if is-private password "")]
        (create-game {:arena-id arena-id
                      :start-time utc-time
                      :num-rounds num-rounds
                      :round-length round-ms
                      :round-intermission intermission-ms
                      :max-players num-players
                      :password password
                      :is-private is-private
                      :game-type game-type
                      :name game-name
                      :on-success #(re-frame/dispatch [:set-modal nil])
                      :on-error #(callback-error % cmpnt-state)})))))

(defn render-password [cmpnt-state]
  (when (is-private? (:game-status @cmpnt-state))
    [text-input-with-label {:class "password"
                            :name "password"
                            :state cmpnt-state
                            :label "Password"}]))

(defn create-game-modal [arena-id]
  (let [modal-error (re-frame/subscribe [:modal-error])
        cmpnt-state (reagent/atom initial-cmpnt-state)]
    (fn []
      [:div.modal.create-game-modal
       [:div.title "CREATE GAME"]
       (when @modal-error [:div.modal-error @modal-error])
       [:div.modal-content
        [text-input-with-label {:class "game-name"
                                :name "game-name"
                                :label "Game Name"
                                :state cmpnt-state}]
        [text-input-with-label {:class "number-of-players"
                                :name "number-of-players"
                                :label "Number of Players"
                                :state cmpnt-state}]
        [text-input-with-label {:class "number-of-rounds"
                                :name "number-of-rounds"
                                :label "Number of Rounds"
                                :state cmpnt-state}]
        [text-input-with-label {:class "intermission-time"
                                :name "intermission-time"
                                :label "Intermission Time"
                                :state cmpnt-state}]
        [datepicker {:class "start-time"
                     :name "start-time"
                     :label "Start Time"
                     :state cmpnt-state}]
        [text-input-with-label {:class "round-time"
                                :name "round-time"
                                :label "Round Time"
                                :state cmpnt-state}]
        [radio-select {:class "game-status"
                       :name "game-status"
                       :label "Game Status"
                       :state cmpnt-state
                       :radios radios}]
        [render-password cmpnt-state]]
       [:div.action-buttons
        [cancel-modal-input]
        [submit-modal-input
         "CREATE"
         #(on-submit-form-valid arena-id cmpnt-state)]]])))
