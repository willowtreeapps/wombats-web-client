(ns wombats-web-client.components.modals.delete-wombat-modal
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.utils.forms :refer [cancel-modal-input]]
            [wombats-web-client.events.user :refer [delete-wombat]]))

(defn delete-wombat-modal [id]
  (fn [id]
    [:div {:class "modal delete-wombat-modal"}
     [:div.title "DELETE WOMBAT"]
     [:div.desc "You are about to delete this bot. Are you sure you want to do this?"]
     [:div.action-buttons
      [cancel-modal-input]
      [:input.modal-button {:type "button"
                            :value "DELETE"
                            :on-click #(delete-wombat id)}]]]))
