(ns wombats-web-client.components.modals.edit-wombat-modal
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.events.user :refer [edit-wombat-by-id]]
            [wombats-web-client.utils.forms :refer [text-input-with-label
                                                    cancel-modal-input]]))

(defn callback-success [cmpnt-state]
  "closes modal on success"
  (swap! cmpnt-state assoc :error nil)
  (re-frame/dispatch [:set-modal nil]))

(defn callback-error [state]
  "says error, persists modal"
  (swap! state assoc :error "ERROR"))


(defn edit-wombat-modal [wombat-id name url]
  (let [cmpnt-state (reagent/atom {:wombat-name name
                                   :wombat-url url
                                   :error nil})]
    (fn []
      (let [wombat-name (get @cmpnt-state :wombat-name)
            wombat-url (get @cmpnt-state :wombat-url)
            error (get @cmpnt-state :error)]
        [:div {:class "modal edit-wombat-modal"}
         [:div.title "EDIT WOMBAT"]
         (if error [:div error])
         [:form
          [text-input-with-label {:name "wombat-name"
                                  :label "Wombat Name"
                                  :state cmpnt-state}]
          [text-input-with-label {:name "wombat-url"
                                  :label "Wombat URL"
                                  :state cmpnt-state}]
          [:div.action-buttons
           [cancel-modal-input]
           [:input.modal-button {:type "button"
                                 :value "SAVE"
                                 :on-click (fn []
                                             (edit-wombat-by-id
                                              wombat-name
                                              wombat-url
                                              wombat-id
                                              #(callback-success cmpnt-state)
                                              #(callback-error cmpnt-state)))}]]]]))))
