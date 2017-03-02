(ns wombats-web-client.components.modals.join-wombat-modal
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [pushy.core :as pushy]
            [wombats-web-client.components.modals.game-full-modal :refer [game-full-modal]]
            [wombats-web-client.events.games :refer [join-open-game
                                                     get-open-games
                                                     get-all-games]]
            [wombats-web-client.components.text-input :refer [text-input-with-label]]
            [wombats-web-client.utils.forms :refer [submit-modal-input
                                                    cancel-modal-input]]
            [wombats-web-client.utils.games :refer [get-occupied-colors]]
            [wombats-web-client.utils.errors :refer [required-field-error
                                                     wombat-color-missing]]
            [wombats-web-client.constants.colors :refer [colors-8]]
            [wombats-web-client.utils.functions :refer [in?]]
            [wombats-web-client.routes :refer [history]]))


(defonce initial-cmpnt-state {:show-dropdown false
                              :error nil
                              :wombat-name nil
                              :wombat-name-error nil
                              :wombat-id nil
                              :wombat-color nil
                              :password ""
                              :password-error nil})

(def callback-success (fn [game-id wombat-id wombat-color password]
                        "closes modal on success"
                        (re-frame/dispatch [:add-join-selection {:game-id game-id
                                                                 :wombat-id wombat-id
                                                                 :wombat-color wombat-color}])
                        (get-all-games)
                        (re-frame/dispatch [:update-modal-error nil])
                        (re-frame/dispatch [:set-modal nil])
                        (pushy/set-token! history (str "/games/" game-id))))


(def callback-error (fn [error cmpnt-state]
                      (let [error-code (:code (:response error))
                            is-game-full? (= error-code 101001)]

                        (when is-game-full?
                          (re-frame/dispatch [:set-modal {:fn #(game-full-modal)
                                                          :show-overlay? true}]))

                        (re-frame/dispatch [:update-modal-error (:message (:response error))])
                        (get-open-games)
                        (reset! cmpnt-state initial-cmpnt-state))))

(defn on-wombat-selection [cmpnt-state id name]
  (swap! cmpnt-state assoc :wombat-id id
                           :show-dropdown false
                           :wombat-name name))

(defn on-select-click [cmpnt-state]
  (let [wombat-name-missing (nil? (:wombat-name @cmpnt-state))
        show-dropdown (:show-dropdown @cmpnt-state)
        error-state (when wombat-name-missing required-field-error)]
    (when show-dropdown
       (swap! cmpnt-state assoc :wombat-name-error error-state))

    (swap! cmpnt-state assoc :show-dropdown (not show-dropdown))))

(defn on-select-focus [cmpnt-state]
  (swap! cmpnt-state assoc :wombat-name-error nil))

(defn wombat-options [wombat cmpnt-state]
  (let [{:keys [wombat/name wombat/id]} wombat]
    [:li {:key id
          :onClick #(on-wombat-selection cmpnt-state id name)} name]))

(defn select-input-with-label [cmpnt-state]
  (let [{:keys [show-dropdown wombat-name wombat-name-error]} @cmpnt-state
        my-wombats @(re-frame/subscribe [:my-wombats])]
    [:div.select-wombat
     [:label.label.select-wombat-label "Select Wombat"]
     [:div.placeholder
      {:class (clojure.string/join " "[(when-not wombat-name "unselected")
                                       (when wombat-name-error "field-error")])
       :tab-index "0"
       :on-click #(on-select-click cmpnt-state)
       :on-focus #(on-select-focus cmpnt-state)}
      [:div.text {:class (when-not wombat-name "unselected")}
       (str (if-not wombat-name "Select Wombat" wombat-name))]
      [:img.icon-arrow {:class (when show-dropdown "open-dropdown")
                        :src "/images/icon-arrow.svg"}]]
     (when wombat-name-error
       [:div.inline-error wombat-name-error])
     (when show-dropdown
       [:div.dropdown-wrapper
        (for [wombat my-wombats] (wombat-options wombat cmpnt-state))])]))

(defn on-wombat-image-select [cmpnt-state color-text]
  (swap! cmpnt-state assoc :wombat-color color-text :wombat-color-error nil))

(defn wombat-img [color color-selected cmpnt-state occupied-colors]
  (let [{:keys [color-text color-hex]} color
        disabled (in? occupied-colors color-text)
        selected (= color-text color-selected)
        on-click-fn (if disabled (fn []) #(on-wombat-image-select cmpnt-state color-text))]
    [:div.wombat-img-wrapper {:key color-text}
     [:div.disabled {:class (when (in? occupied-colors color-text) "display")}]
     [:div.selected {:class (when selected "display")
                     :style {:background color-hex
                             :opacity "0.8"}}]
     [:img.selected-icon {:class (when selected "display") :src "/images/play.svg"}]
     [:img.wombat {:src (str "/images/wombat_" color-text "_right.png")
            :onClick on-click-fn}]]))

(defn select-wombat-color [cmpnt-state selected-color occupied-colors]
  (let [wombat-color-error (:wombat-color-error @cmpnt-state)]
    [:div.select-color
     [:label.label "Select Color"]
     [:div.colors
      (for [color colors-8]
        ^{:key color} [wombat-img color selected-color cmpnt-state occupied-colors])]
     (when wombat-color-error 
       [:div.inline-error wombat-color-error])]))

(defn private-game-password
  [game cmpnt-state]
  [:div.private-game-container
   [:p.private-game-msg "This is a private game. Please enter the password to join."]
   [text-input-with-label {:name "password"
                           :label "Password"
                           :state cmpnt-state
                           :is-password true}]])

(defn correct-privacy-settings [is-private password-error]
  (cond 
   is-private (not (true? password-error)) ;; if it's private and there's no error, can submit
   (not is-private) true)) ;; if the game isn't private, password state is irrelevant

(defn on-submit-form-valid? [{:keys [game-id is-private cmpnt-state]}]
  (let [{:keys [wombat-name
                wombat-name-error
                wombat-color
                wombat-color-error
                wombat-id
                password
                password-error]} @cmpnt-state]

    (when  (nil? wombat-color) 
      (swap! cmpnt-state assoc :wombat-color-error wombat-color-missing))

    (when (nil? wombat-name)
      (swap! cmpnt-state assoc :wombat-name-error required-field-error))
     
    (when (and is-private (clojure.string/blank? password)) 
      (swap! cmpnt-state assoc :password-error required-field-error))

    (when (and wombat-name wombat-color (correct-privacy-settings is-private password-error))
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
        cmpnt-state (reagent/atom initial-cmpnt-state)
        game (re-frame/subscribe [:game/details game-id])] ;; not included in render fn
    (reagent/create-class
     {:component-will-unmount #(re-frame/dispatch [:update-modal-error nil])
      :display-name "join-game-modal"
      :reagent-render

      (fn [] ;; render function
        (let [{:keys [error wombat-id wombat-color password]} @cmpnt-state
              error @modal-error
              is-private (:game/is-private @game)
              occupied-colors (get-occupied-colors @game)
              title (if is-private "JOIN PRIVATE GAME" "JOIN GAME")]
          [:div {:class "modal join-wombat-modal"} ;; starts hiccup
           [:div.title title]
           (when error [:div.modal-error error])
           (when is-private
             [private-game-password @game cmpnt-state])
           [select-input-with-label cmpnt-state]
           [select-wombat-color cmpnt-state wombat-color occupied-colors]
           [:div.action-buttons
            [cancel-modal-input]
            [submit-modal-input "JOIN" #(on-submit-form-valid? {:game-id game-id
                                                                    :is-private is-private
                                                                    :cmpnt-state cmpnt-state})]]]))})))
