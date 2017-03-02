(ns wombats-web-client.components.modals.add-wombat-modal
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.events.user :refer [create-new-wombat]]
            [wombats-web-client.utils.forms :refer [text-input-with-label
                                                    cancel-modal-input]]))

(defn callback-success [state]
  "closes modal on success"
  (re-frame/dispatch [:update-modal-error nil])
  (re-frame/dispatch [:set-modal nil]))

(defn on-submit-form-valid? [cmpnt-state username]
  (let [{:keys [wombat-name
                wombat-repo-name
                wombat-file-path]} @cmpnt-state
        url (str username "/" 
                 wombat-repo-name "/contents/" 
                 wombat-file-path)]
    (when (nil? wombat-name)
      (swap! cmpnt-state assoc :wombat-name-error "This field is required."))
    (when (nil? wombat-repo-name)
      (swap! cmpnt-state assoc :wombat-repo-name-error "This field is required."))
    (when (nil? wombat-file-path)
      (swap! cmpnt-state assoc :wombat-file-path-error "This field is required."))

    (when (and wombat-name wombat-repo-name wombat-file-path)
      (create-new-wombat wombat-name url #(callback-success cmpnt-state)))))

(defn add-wombat-modal []
  (let [cmpnt-state (reagent/atom {:wombat-name nil
                                   :wombat-repo-name nil
                                   :wombat-file-path nil
                                   :wombat-name-error nil
                                   :wombat-repo-name-error nil
                                   :wombat-file-path-error nil
                                   :error nil})
        modal-error (re-frame/subscribe [:modal-error])
        current-user (re-frame/subscribe [:current-user])]
    (reagent/create-class
     {:component-will-unmount #(re-frame/dispatch [:update-modal-error nil])
      :display-name "add-wombat-modal"
      :reagent-render
      (fn []
        (let [{:keys [wombat-name wombat-repo-name wombat-file-path]} @cmpnt-state
              error @modal-error
              username (:user/github-username @current-user)]
          [:div {:class "modal add-wombat-modal"}
           [:div.title "ADD WOMBAT"]
           (when error [:div.modal-error error])
           [:form
            [text-input-with-label {:name "wombat-name"
                                    :label "Wombat Name"
                                    :state cmpnt-state}]
            [text-input-with-label {:name "wombat-repo-name"
                                    :label "Wombat Repository Name"
                                    :state cmpnt-state}]
            [text-input-with-label {:name "wombat-file-path"
                                    :label "Wombat File Path"
                                    :state cmpnt-state}]
            [:div.action-buttons
             [cancel-modal-input]
             [:input.modal-button {:type "button"
                                   :value "ADD"
                                   :on-click #(on-submit-form-valid? cmpnt-state username)}]]]]))})))
