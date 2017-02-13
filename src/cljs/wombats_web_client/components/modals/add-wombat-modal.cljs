(ns wombats-web-client.components.modals.add-wombat-modal
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.events.user :refer [create-new-wombat]]
            [wombats-web-client.utils.forms :refer [text-input-with-label
                                                    cancel-modal-input]]))

(defn callback-success [state]
  "closes modal on success"
  (swap! state assoc :error nil)
  (re-frame/dispatch [:set-modal nil]))

(defn callback-error [state]
  "says error, persists modal"
  (swap! state assoc :error "ERROR"))

(defn add-wombat-modal []
  (let [cmpnt-state (reagent/atom {:wombat-name nil
                                   :wombat-url nil
                                   :error nil})]
    (fn []
      (let [{:keys [error wombat-name wombat-url]} @cmpnt-state]
        [:div {:class "modal add-wombat-modal"}
         [:div.title "ADD WOMBAT"]
         (when error [:div error])
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
                                 :value "ADD"
                                 :on-click (fn []
                                             (create-new-wombat
                                              wombat-name
                                              wombat-url
                                              #(callback-success cmpnt-state)
                                              #(callback-error cmpnt-state)))}]]]]))))
