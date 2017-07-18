(ns wombats-web-client.components.modals.delete-game-modal
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.utils.forms :refer [cancel-modal-input
                                                    submit-modal-input]]
            [wombats-web-client.events.games :refer [delete-game]]))

(defonce delete-desc
  "You are about to delete this game. Are you sure you want to do this?")

(def error (reagent/atom nil))

(defn reset-state []
  (reset! error nil))

(defn callback-success [cmpnt-state]
  "closes modal on success"
  (re-frame/dispatch [:update-modal-error nil])
  (re-frame/dispatch [:set-modal nil]))

(defn delete-game-modal [id]
  (let [modal-error (re-frame/subscribe [:modal-error])
        cmpnt-state (reagent/atom {:error nil})]
    (reagent/create-class
     {:component-will-unmount #(re-frame/dispatch [:update-modal-error nil])
      :reagent-render
      (fn []
        [:div {:class "modal delete-game-modal"}
         [:div.title "DELETE GAME"]
         (when @modal-error [:div.modal-error @modal-error])
         [:div.modal-content
          [:div.desc delete-desc]]
         [:div.action-buttons
          [cancel-modal-input]
          [submit-modal-input "DELETE"
           (fn []
             (delete-game
              id
              #(callback-success cmpnt-state)))]]])})))
