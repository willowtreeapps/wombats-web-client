(ns wombats-web-client.components.modals.delete-wombat-modal
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.utils.forms :refer [cancel-modal-input]]
            [wombats-web-client.events.user :refer [delete-wombat]]))

(def error (reagent/atom nil))

(defn callback-success []
  "closes modal on success"
  (reset! error nil)
  (re-frame/dispatch [:set-modal nil]))

(defn callback-error []
  "says error, persists modal"
  (reset! error "ERROR"))

(defn delete-wombat-modal [id]
  (fn []
    [:div {:class "modal delete-wombat-modal"}
     [:div.title "DELETE WOMBAT"]
     [:div.desc "You are about to delete this wombat. Are you sure you want to do this?"]
     [:div.action-buttons
      [cancel-modal-input]
      [:input.modal-button {:type "button"
                            :value "DELETE"
                            :on-click #(delete-wombat id callback-success callback-error)}]]]))
