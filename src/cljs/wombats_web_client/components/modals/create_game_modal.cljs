(ns wombats-web-client.components.modals.create-game-modal
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [cljs-time.core :as time]
            [wombats-web-client.utils.forms
             :refer [submit-modal-input
                     cancel-modal-input]]
            [wombats-web-client.components.text-input
             :refer [text-input-with-label]]
            [wombats-web-client.components.datepicker
             :refer [datepicker]]
            [wombats-web-client.components.radio-select
             :refer [radio-select]]
            [wombats-web-client.utils.errors :refer [get-error-message
                                                     required-field-error
                                                     not-an-integer]]
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

(defn input-error! [input test-fn cmpnt-state error-key error]
  (when (test-fn input)
    (swap! cmpnt-state assoc error-key error)))


(defn has-value? [value-list]
  (reduce (fn [truth value]
            (if (some? value)
              truth
              (and truth false))) true value-list))

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
        local-time (time/local-date-time year-num month-num day-num hour-num min-num)
        utc-time-without-format (.toUTCIsoString (goog.date.UtcDateTime.fromTimestamp
                                                  (.getTime local-time))  true)
        utc-split (clojure.string/split utc-time-without-format #" ")]
    (clojure.string/join "T" utc-split)))

(defn check-for-errors [cmpnt-state num-rounds num-players]
  (let [{:keys [game-name
                game-name-error
                number-of-players
                number-of-players-error
                number-of-rounds
                number-of-rounds-error
                intermission-time
                intermission-time-error
                start-time
                start-time-error
                round-time
                round-time-error
                game-status
                password
                password-error]} @cmpnt-state]

    (input-error! game-name
                  clojure.string/blank?
                  cmpnt-state
                  :game-name-error
                  required-field-error)
    (input-error! number-of-players
                  clojure.string/blank?
                  cmpnt-state
                  :number-of-players-error
                  required-field-error)
    (input-error! num-players
                  #(not (integer? %))
                  cmpnt-state
                  :number-of-players-error
                  not-an-integer)
    (input-error! number-of-rounds
                  clojure.string/blank?
                  cmpnt-state
                  :number-of-rounds-error
                  required-field-error)
    (input-error! num-rounds
                  #(not (integer? %))
                  cmpnt-state
                  :number-of-rounds-error
                  not-an-integer)
    (input-error! intermission-time
                  clojure.string/blank?
                  cmpnt-state
                  :intermission-time-error
                  required-field-error)
    (input-error! start-time
                  clojure.string/blank?
                  cmpnt-state
                  :start-time-error
                  required-field-error)
    (input-error! round-time
                  clojure.string/blank?
                  cmpnt-state
                  :round-time-error
                  required-field-error)
    (input-error! password
                  #(and (= game-status "private") (clojure.string/blank? %))
                  cmpnt-state
                  :password-error
                  required-field-error)))

(defn is-private? [game-status]
  (= "private" game-status))

(defn check-private [game-status password]
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
                not-blank (has-value? [game-name
                                       number-of-players
                                       number-of-rounds
                                       intermission-time
                                       start-time])
                correct-private-input (check-private game-status password)
                num-rounds (js/parseInt number-of-rounds)
                num-players (js/parseInt number-of-players)
                valid-answers (and (integer? num-rounds)
                                   (integer? num-players)
                                   correct-private-input)
                ready-to-submit (and not-blank valid-answers)]

    (check-for-errors cmpnt-state num-rounds num-players)

    (when ready-to-submit
      (let [intermission-ms (get-milliseconds intermission-time)
            round-ms (get-milliseconds round-time)
            utc-time (get-utc start-time)
            game-type :high-score ;; hardcoded for now
            is-private (is-private? game-status)
            password (if is-private password "")]
        (print utc-time)
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
        [submit-modal-input "CREATE" #(on-submit-form-valid arena-id cmpnt-state)]]])))
