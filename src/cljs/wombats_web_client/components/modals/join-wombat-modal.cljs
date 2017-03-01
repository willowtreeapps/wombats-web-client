(ns wombats-web-client.components.modals.join-wombat-modal
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [pushy.core :as pushy]
            [wombats-web-client.components.modals.game-full-modal :refer [game-full-modal]]
            [wombats-web-client.events.games :refer [join-open-game
                                                     get-open-games
                                                     get-all-games]]
            [wombats-web-client.utils.forms :refer [text-input-with-label
                                                    cancel-modal-input]]
            [wombats-web-client.utils.games :refer [get-occupied-colors]]
            [wombats-web-client.constants.colors :refer [colors-8]]
            [wombats-web-client.utils.functions :refer [in?]]
            [wombats-web-client.routes :refer [history]]))


(defonce initial-cmpnt-state {:show-dropdown false
                              :error nil
                              :wombat-name nil
                              :wombat-id nil
                              :wombat-color nil
                              :password ""})

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
  (swap! cmpnt-state assoc :wombat-id id)
  (swap! cmpnt-state assoc :show-dropdown false)
  (swap! cmpnt-state assoc :wombat-name name))

(defn wombat-options [wombat cmpnt-state]
  (let [{:keys [wombat/name wombat/id]} wombat]
    [:li {:key id
          :onClick #(on-wombat-selection cmpnt-state id name)} name]))

(defn select-input-with-label [cmpnt-state]
  (let [{:keys [show-dropdown wombat-name]} @cmpnt-state
        my-wombats @(re-frame/subscribe [:my-wombats])]
    [:div.select-wombat
     [:label.label.select-wombat-label "Select Wombat"]
     [:div.placeholder
      {:class (when-not wombat-name "unselected")
       :onClick #(swap! cmpnt-state assoc :show-dropdown (not show-dropdown))}
      [:div.text {:class (when-not wombat-name "unselected")}
       (str (if-not wombat-name "Select Wombat" wombat-name))]
      [:img.icon-arrow {:class (when show-dropdown "open-dropdown")
                        :src "/images/icon-arrow.svg"}]]
     (when show-dropdown
       [:div.dropdown-wrapper
        (for [wombat my-wombats] (wombat-options wombat cmpnt-state))])]))

(defn wombat-img [color color-selected cmpnt-state occupied-colors]
  (let [{:keys [color-text color-hex]} color
        disabled (in? occupied-colors color-text)
        selected (= color-text color-selected)
        on-click-fn (if disabled (fn []) #(swap! cmpnt-state assoc :wombat-color color-text))]
    [:div.wombat-img-wrapper {:key color-text}
     [:div.disabled {:class (when (in? occupied-colors color-text) "display")}]
     [:div.selected {:class (when selected "display")
                     :style {:background color-hex
                             :opacity "0.8"}}]
     [:img.selected-icon {:class (when selected "display") :src "/images/play.svg"}]
     [:img.wombat {:src (str "/images/wombat_" color-text "_right.png")
            :onClick on-click-fn}]]))

(defn select-wombat-color [cmpnt-state selected-color occupied-colors]
  [:div.select-color
   [:label.label "Select Color"]
   [:div.colors
    (for [color colors-8]
              ^{:key color} [wombat-img color selected-color cmpnt-state occupied-colors])]])

(defn private-game-password
  [game cmpnt-state]
  [:div.private-game-container
   [:p.private-game-msg "This is a private game. Please enter the password to join."]
   [:label.label "Password"]
   [:div.text-input-wrapper
    [:input.input {:type "password"
                   :value (:password @cmpnt-state)
                   :on-change #(swap! cmpnt-state assoc :password (-> % .-target .-value))}]]])

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
           (when (get @game :game/is-private)
             [private-game-password @game cmpnt-state])
           [select-input-with-label cmpnt-state]
           [select-wombat-color cmpnt-state wombat-color occupied-colors]
           [:div.action-buttons
            [cancel-modal-input]
            [:input.modal-button {:type "button"
                                  :value "JOIN"
                                  :on-click (fn []
                                              (join-open-game
                                               game-id
                                               wombat-id
                                               wombat-color
                                               password
                                               #(callback-success game-id
                                                                  wombat-id
                                                                  wombat-color
                                                                  password)
                                               #(callback-error % cmpnt-state)))}]]]))})))
