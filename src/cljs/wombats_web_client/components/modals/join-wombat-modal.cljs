(ns wombats-web-client.components.modals.join-wombat-modal
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.events.games :refer [join-open-game]]
            [wombats-web-client.utils.forms :refer [text-input-with-label
                                                    cancel-modal-input]]
            [wombats-web-client.constants.colors :refer [colors-8]]))

(def callback-success (fn [game-id wombat-id wombat-color cmpnt-state]
                        "closes modal on success"
                        (re-frame/dispatch [:add-join-selection {:game-id game-id
                                                                 :wombat-id wombat-id
                                                                 :wombat-color wombat-color}])
                        (re-frame/dispatch [:update-modal-error nil])
                        (re-frame/dispatch [:set-modal nil])))

(defn on-wombat-selection [cmpnt-state id name]
  (swap! cmpnt-state assoc :wombat-id id)
  (swap! cmpnt-state assoc :show-dropdown false)
  (swap! cmpnt-state assoc :wombat-name name))

(defn wombat-options [wombat cmpnt-state]
  (let [{:keys [name id]} wombat]
    [:li {:key id
          :onClick #(on-wombat-selection cmpnt-state id name)} name]))

(defn select-input-with-label [cmpnt-state]
  (let [{:keys [show-dropdown wombat-name]} @cmpnt-state
        my-wombats @(re-frame/subscribe [:my-wombats])]
    [:div.select-wombat
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

(defn in? [coll element]
  (some #(= element %) coll))

(defn wombat-img [color color-selected cmpnt-state occupied-colors]
  (let [{:keys [color-text color-hex]} color
        disabled (in? occupied-colors color-text)
        on-click-fn (if disabled (fn []) #(swap! cmpnt-state assoc :wombat-color color-text))]
    [:div.wombat-img-wrapper {:key color-text}
     [:div.disabled {:class (when (in? occupied-colors color-text) "display")}] 
     [:div.selected {:class (when (= color-text color-selected) "display")
                     :style {:background color-hex
                             :opacity "0.8"}}
      [:img {:src "/images/checkmark.svg"}]]
     [:img.wombat {:src (str "/images/wombat_" color-text "_right.png")
            :onClick on-click-fn}]]))

(defn select-wombat-color [cmpnt-state selected-color occupied-colors]
  [:div.select-color
   [:label.label "Select Color"]
   [:div.colors
    (for [color colors-8]
              ^{:key color} [wombat-img color selected-color cmpnt-state occupied-colors])]])

(defn join-wombat-modal [game-id occupied-colors]
  (let [modal-error (re-frame/subscribe [:modal-error])
        cmpnt-state (reagent/atom {:show-dropdown false
                                   :error nil
                                   :wombat-name nil
                                   :wombat-id nil
                                   :wombat-color nil})] ;; not included in render fn
    (reagent/create-class
     {:component-will-unmount #(re-frame/dispatch [:update-modal-error nil])
      :display-name "join-game-modal"
      :reagent-render

      (fn [] ;; render function
        (let [{:keys [error wombat-id wombat-color]} @cmpnt-state
              error @modal-error]
          [:div {:class "modal join-wombat-modal"} ;; starts hiccup
           [:div.title "JOIN GAME"]
           (when error [:div.modal-error error])
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
                                               #(callback-success game-id
                                                                  wombat-id
                                                                  wombat-color
                                                                  cmpnt-state)))}]]]))})))
