(ns wombats-web-client.components.modals.join-wombat-modal
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.events.games :refer [join-open-game]]
            [wombats-web-client.utils.forms :refer [text-input-with-label
                                                    cancel-modal-input]]
            [wombats-web-client.constants.colors :refer [colors-8]]))

(def wombat-selection (reagent/atom nil))
(def wombat-color-selection (reagent/atom nil))
(def wombat-placeholder (reagent/atom nil))
(def show-wombat-dropdown (reagent/atom false))
(def error (reagent/atom nil))

(defn clear-local-state []
  (reset! wombat-selection nil)
  (reset! wombat-color-selection nil)
  (reset! wombat-placeholder nil)
  (reset! show-wombat-dropdown false)
  (reset! error nil))

(defn callback-success []
  "closes modal on success"
  (reset! error nil)
  (re-frame/dispatch [:set-modal nil]))

(defn callback-error []
  "says error, persists modal"
  (reset! error "ERROR"))

(defn on-wombat-selection [id name]
  (reset! wombat-selection id)
  (reset! show-wombat-dropdown false)
  (reset! wombat-placeholder name))

(defn wombat-options [wombat]
  [:li {:key (:id wombat)
        :onClick #(on-wombat-selection (:id wombat) (:name wombat))} (:name wombat)])

(defn select-input-with-label []
  (let [my-wombats (re-frame/subscribe [:my-wombats])]
    [:div.select-wombat
     [:div.placeholder 
      {:onClick #(reset! show-wombat-dropdown (not @show-wombat-dropdown))}
      [:div.text {:class (when (nil? @wombat-placeholder) "unselected")} 
       (str (if (nil? @wombat-placeholder) "Select Wombat" @wombat-placeholder))]
      [:img.icon-arrow {:class (when @show-wombat-dropdown "open-dropdown")
                        :src "/images/icon-arrow.svg"}]]
     (when @show-wombat-dropdown
       [:div.dropdown-wrapper
        (map wombat-options @my-wombats)])]))

(defn wombat-img [color color-selected]
  [:div.wombat-img-wrapper {:key color}
   [:div.selected {:class (when (= color color-selected) "display")
                   :style {:background color
                           :opacity "0.8"}}]
   [:img {:src (str "/images/wombat_" color "_right.png")
          :onClick #(reset! wombat-color-selection color)}]])

(defn select-wombat-color []
  (let [selected-color @wombat-color-selection]
    [:div.select-color
     [:label.label "Select Color"]
     [:div.colors
      (doall (for [color colors-8]
               [wombat-img color selected-color]))]]))

(defn join-wombat-modal [game-id]
  (fn []
    [:div {:class "modal join-wombat-modal"}
     [:div.title "JOIN GAME"]
     (if (not (nil? @error)) [:div @error])
     [select-input-with-label]
     [select-wombat-color]
     [:div.action-buttons
      [cancel-modal-input clear-local-state]
      [:input.modal-button {:type "button"
                            :value "JOIN"
                            :on-click #(join-open-game game-id @wombat-selection @wombat-color-selection callback-success callback-error)}]]]))
