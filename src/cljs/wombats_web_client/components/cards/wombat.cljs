(ns wombats-web-client.components.cards.wombat
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.components.modals.delete-wombat-modal :refer [delete-wombat-modal]]
            [wombats-web-client.components.modals.edit-wombat-modal :refer [edit-wombat-modal]]
            [wombats-web-client.constants.colors :refer [colors-8]]))

(defn open-delete-wombat-modal [id]
  (fn []
    (re-frame/dispatch [:set-modal #(delete-wombat-modal id)])))

(defn open-edit-wombat-modal [id name url]
  (fn []
    (re-frame/dispatch [:set-modal #(edit-wombat-modal id name url)])))

(defn wombat-information
  [isUserHovering? name id url]
  [:div.wombat-information {:class (when @isUserHovering? "hovering")}
   [:div.name name]
   (when @isUserHovering?
     [:div.hover-state-edit
      [:input.edit {:type "button"
                    :value "EDIT"
                    :on-click (open-edit-wombat-modal id name url)}]
      [:input.delete {:type "button"
                      :value "DELETE"
                      :on-click (open-delete-wombat-modal id)}]])])

(defn root [wombat]
  (let [isUserHovering? (reagent/atom false)
        color (reagent/atom nil)]
    [:div.wombat-card {:key (:id wombat)
                       :onMouseOver #(reset! isUserHovering? true)
                       :onMouseOut #(reset! isUserHovering? false)}
     [:img.wombat-image {:src (str "/images/wombats/wombat_purple_right.png")}]
     [wombat-information isUserHovering? (:name wombat) (:id wombat) (:url wombat)]]))
