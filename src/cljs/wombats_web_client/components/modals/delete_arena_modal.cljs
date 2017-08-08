(ns wombats-web-client.components.modals.delete-arena-modal
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.events.arenas :refer [get-arenas]]
            [wombats-web-client.utils.errors :refer [get-error-message]]
            [wombats-web-client.utils.forms :refer [cancel-modal-input
                                                    submit-modal-input]]
            [wombats-web-client.events.arenas :refer [delete-arena]]))

(defonce delete-desc
  "You are about to delete this arena. Are you sure you want to do this?")

(def error (reagent/atom nil))

(defn reset-state []
  (reset! error nil))

(defn callback-success [cmpnt-state]
  "closes modal on success"
  (get-arenas)
  (re-frame/dispatch [:update-modal-error nil])
  (re-frame/dispatch [:set-modal nil]))

(defn delete-arena-modal [id]
  (let [modal-error (re-frame/subscribe [:modal-error])
        cmpnt-state (reagent/atom {:error nil})]
    (reagent/create-class
     {:component-will-unmount #(re-frame/dispatch [:update-modal-error nil])
      :reagent-render
      (fn []
        [:div {:class "modal delete-game-modal"}
         [:div.title "DELETE ARENA"]
         (println id)
         (when @modal-error [:div.modal-error @modal-error])
         [:div.modal-content
          [:div.desc delete-desc]]
         [:div.action-buttons
          [cancel-modal-input]
          [submit-modal-input "DELETE"
           (fn [id]
             (delete-arena
              id
              #(callback-success cmpnt-state)
              #(re-frame/dispatch
                [:update-modal-error (get-error-message %)])))]]])})))

(defn render-delete-arena-modal [id]
  [delete-arena-modal id])
