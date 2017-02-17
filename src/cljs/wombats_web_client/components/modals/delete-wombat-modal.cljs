(ns wombats-web-client.components.modals.delete-wombat-modal
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.utils.forms :refer [cancel-modal-input]]
            [wombats-web-client.events.user :refer [delete-wombat]]))

(def error (reagent/atom nil))

(defn reset-state []
  (reset! error nil))

(defn callback-success [cmpnt-state]
  "closes modal on success"
  (re-frame/dispatch [:update-modal-error nil])
  (re-frame/dispatch [:set-modal nil]))

(defn delete-wombat-modal [id]
  (let [modal-error (re-frame/subscribe [:modal-error])
        cmpnt-state (reagent/atom {:error nil})]
    (reagent/create-class
     {:component-will-unmount #(re-frame/dispatch [:update-modal-error nil])
      :reagent-render
      (fn []
        [:div {:class "modal delete-wombat-modal"}
         [:div.title "DELETE WOMBAT"]
         [:div.modal-error @modal-error]
         [:div.desc "You are about to delete this wombat. Are you sure you want to do this?"]
         [:div.action-buttons
          [cancel-modal-input]
          [:input.modal-button {:type "button"
                                :value "DELETE"
                                :on-click (fn []
                                            (delete-wombat
                                             id
                                             #(callback-success cmpnt-state)))}]]])})))
