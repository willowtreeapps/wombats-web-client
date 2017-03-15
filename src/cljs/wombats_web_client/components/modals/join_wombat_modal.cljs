(ns wombats-web-client.components.modals.join-wombat-modal
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [pushy.core :as pushy]
            [wombats-web-client.components.modals.game-full-modal
             :refer [game-full-modal]]
            [wombats-web-client.components.modals.game-started-modal
             :refer [game-started-modal]]
            [wombats-web-client.components.select-input
             :refer [select-input]]
            [wombats-web-client.events.games :refer [join-open-game
                                                     get-open-games
                                                     get-all-games]]
            [wombats-web-client.components.text-input
             :refer [text-input-with-label]]
            [wombats-web-client.utils.forms :refer [submit-modal-input
                                                    cancel-modal-input
                                                    optionize]]
            [wombats-web-client.utils.games :refer [get-occupied-colors]]
            [wombats-web-client.utils.errors :refer [get-error-message
                                                     is-game-full?
                                                     has-game-started?
                                                     has-field-error?
                                                     required-field-error
                                                     wombat-color-missing]]
            [wombats-web-client.constants.colors :refer [colors-8]]
            [wombats-web-client.utils.functions :refer [in?]]
            [wombats-web-client.routes :refer [history]]))

(defonce private-game-prompt
  "This is a private game. Please enter the password to join.")

(defonce initial-cmpnt-state {:show-dropdown false
                              :error nil
                              :wombat-id nil
                              :wombat-id-error nil
                              :wombat-color nil
                              :wombat-color-error nil
                              :password ""
                              :password-error nil})

(def callback-success (fn [game-id wombat-id wombat-color password]
                        "closes modal on success"
                        (re-frame/dispatch
                         [:add-join-selection {:game-id game-id
                                               :wombat-id wombat-id
                                               :wombat-color wombat-color}])
                        (re-frame/dispatch [:update-modal-error nil])
                        (re-frame/dispatch [:set-modal nil])
                        (pushy/set-token! history (str "/games/" game-id))))


(def callback-error
  (fn [error cmpnt-state]
    (let [game-full (is-game-full? error)
          game-started (has-game-started? error)
          password-error (has-field-error? error :password)
          wombat-color-error (has-field-error? error :wombat-color)]

      (get-all-games)
      (cond
       game-full
       (re-frame/dispatch [:set-modal {:fn #(game-full-modal)
                                       :show-overlay? true}])

       game-started
       (re-frame/dispatch [:set-modal {:fn #(game-started-modal)
                                       :show-overlay? true}])

       password-error
       (swap! cmpnt-state assoc :password-error (get-error-message error))

       wombat-color-error
       (swap! cmpnt-state assoc
              :wombat-color-error (get-error-message error)
              :wombat-color nil)
       :else
       (do
         (re-frame/dispatch [:update-modal-error (get-error-message error)])
         (reset! cmpnt-state initial-cmpnt-state))))))

(defn on-wombat-image-select [cmpnt-state color-text]
  (swap! cmpnt-state assoc :wombat-color color-text :wombat-color-error nil))

(defn wombat-img [color color-selected cmpnt-state occupied-colors]
  (let [{:keys [color-text color-hex]} color
        disabled (in? occupied-colors color-text)
        selected (= color-text color-selected)
        on-click-fn (when-not disabled
                      #(on-wombat-image-select cmpnt-state color-text))]
    [:div.wombat-img-wrapper {:key color-text}
     [:div.disabled {:class (when (in? occupied-colors color-text) "display")}]
     [:div.selected {:class (when selected "display")
                     :style {:background color-hex
                             :opacity "0.8"}}]
     [:img.selected-icon {:class (when selected "display")
                          :src "/images/play.svg"}]
     [:img.wombat {:src (str "/images/wombat_" color-text "_right.png")
                   :on-click on-click-fn}]]))

(defn select-wombat-color [cmpnt-state selected-color occupied-colors]
  (let [wombat-color-error (:wombat-color-error @cmpnt-state)]
    [:div.select-color
     [:label.label "Select Color"]
     [:div.colors
      (for [color colors-8]
        ^{:key color}
        [wombat-img color selected-color cmpnt-state occupied-colors])]
     (when wombat-color-error
       [:div.inline-error wombat-color-error])]))

(defn private-game-password
  [game cmpnt-state]
  [:div.private-game-container
   [:p.private-game-msg private-game-prompt]
   [text-input-with-label {:name "password"
                           :label "Password"
                           :state cmpnt-state
                           :is-password true}]])

(defn correct-privacy-settings [is-private password-error]
  (cond
   ;; if it's private and there's no error, can submit
   is-private (false? password-error)

   ;; if the game isn't private, password state is irrelevant
   (not is-private) true))

(defn on-submit-form-valid? [{:keys [game-id is-private cmpnt-state]}]
  (let [{:keys [wombat-color
                wombat-color-error
                wombat-id
                wombat-id-error
                password
                password-error]} @cmpnt-state
                password-blank? (and is-private
                                     (clojure.string/blank? password))
                private-input-correct? (correct-privacy-settings
                                        is-private
                                        password-error)
                ready-to-submit? (and wombat-id
                                      wombat-color
                                      private-input-correct?)]

    (when  (nil? wombat-color)
      (swap! cmpnt-state assoc :wombat-color-error wombat-color-missing))

    (when (nil? wombat-id)
      (swap! cmpnt-state assoc :wombat-id-error required-field-error))

    (when password-blank?
      (swap! cmpnt-state assoc :password-error required-field-error))

    (when ready-to-submit?
      (join-open-game game-id
                      wombat-id
                      wombat-color
                      password
                      #(callback-success game-id
                                         wombat-id
                                         wombat-color
                                         password)
                      #(callback-error % cmpnt-state)))))

(defn join-wombat-modal [game-id]
  (let [modal-error (re-frame/subscribe [:modal-error])
        my-wombats (re-frame/subscribe [:my-wombats])
        cmpnt-state (reagent/atom initial-cmpnt-state)
        games (re-frame/subscribe [:games])] ;; not included in render fn
    (reagent/create-class
     {:component-will-unmount #(re-frame/dispatch [:update-modal-error nil])
      :display-name "join-game-modal"
      :reagent-render

      (fn [] ;; render function
        (let [{:keys [error wombat-id wombat-color password]} @cmpnt-state
              game (get @games game-id)
              error @modal-error
              is-private (:game/is-private game)
              occupied-colors (get-occupied-colors game)
              my-wombat-options (optionize [:wombat/id] [:wombat/name] @my-wombats)
              title (if is-private "JOIN PRIVATE GAME" "JOIN GAME")]
          [:div.modal.join-wombat-modal ;; starts hiccup
           [:div.title title]
           (when error [:div.modal-error error])
           [:div.modal-content
            (when is-private
              [private-game-password game cmpnt-state])
            [select-input {:form-state cmpnt-state
                           :form-key :wombat-id
                           :error-key :wombat-id-error
                           :option-list my-wombat-options
                           :label "Select Wombat"}]
            [select-wombat-color cmpnt-state wombat-color occupied-colors]]
           [:div.action-buttons
            [cancel-modal-input]
            [submit-modal-input "JOIN" #(on-submit-form-valid?
                                         {:game-id game-id
                                          :is-private is-private
                                          :cmpnt-state cmpnt-state})]]]))})))
