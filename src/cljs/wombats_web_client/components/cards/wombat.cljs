(ns wombats-web-client.components.cards.wombat
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.components.modals.delete-wombat-modal :refer [delete-wombat-modal]]
            [wombats-web-client.constants.colors :refer [colors-8]]))

(defn open-delete-wombat-modal [id]
  (fn []
    (re-frame/dispatch [:set-modal #(delete-wombat-modal id)])))

(defn wombat-information [isUserHovering? name id]
  [:div.wombat-information
   [:div.name name]
   (when @isUserHovering?
     [:div.hover-state-edit
      [:input.delete { :type "button"
                      :value "DELETE"
                      :on-click (open-delete-wombat-modal id)}]])])

(defn root [wombat]
  (let [isUserHovering? (reagent/atom false)
        color (reagent/atom nil)]
    [:div.wombat-card {:key (:id wombat)
                       :onMouseOver #(reset! isUserHovering? true)
                       :onMouseOut #(reset! isUserHovering? false)}
     [:img.wombat-image {:src (str "/images/wombat_purple_right.png")}]
     [wombat-information isUserHovering? (:name wombat) (:id wombat)]]))
