(ns wombats-web-client.components.modals.edit-wombat-modal
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.events.user :refer [edit-wombat-by-id]]
            [wombats-web-client.utils.forms :refer [text-input-with-label
                                                    cancel-modal-input]]))

(defn callback-success [cmpnt-state]
  "closes modal on success"
  (re-frame/dispatch [:update-modal-error nil])
  (re-frame/dispatch [:set-modal nil]))

(defn edit-wombat-modal [wombat-id name url]
  (let [modal-error (re-frame/subscribe [:modal-error])
        cmpnt-state (reagent/atom {:wombat-name name
                                   :wombat-url url
                                   :error nil})]
    (reagent/create-class
     {:component-will-unmount #(re-frame/dispatch [:update-modal-error nil])
      :reagent-render
      (fn []
        (let [{:keys [wombat-name wombat-url error]} @cmpnt-state]
          [:div {:class "modal edit-wombat-modal"}
           [:div.title "EDIT WOMBAT"]
           (when @modal-error [:div.modal-error @modal-error])
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
                                                #(callback-success cmpnt-state)))}]]]]))})))
