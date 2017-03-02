(ns wombats-web-client.components.modals.edit-wombat-modal
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.components.text-input :refer [text-input-with-label]]
            [wombats-web-client.events.user :refer [edit-wombat-by-id]]
            [wombats-web-client.utils.forms :refer [submit-modal-input
                                                    cancel-modal-input]]
            [wombats-web-client.utils.errors :refer [required-field-error]]))

(defn callback-success [cmpnt-state]
  "closes modal on success"
  (re-frame/dispatch [:update-modal-error nil])
  (re-frame/dispatch [:set-modal nil]))

(defn on-submit-form-valid? [cmpnt-state wombat-id]
  (let [{:keys [wombat-name
                wombat-url]} @cmpnt-state]
    (when (clojure.string/blank? wombat-name)
      (swap! cmpnt-state assoc :wombat-name-error required-field-error))
    (when (clojure.string/blank? wombat-url)
      (swap! cmpnt-state assoc :wombat-url-error required-field-error))

    (when (and wombat-name wombat-url)
      (edit-wombat-by-id wombat-name wombat-url wombat-id #(callback-success cmpnt-state)))))

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
             [submit-modal-input "SAVE" #(on-submit-form-valid? cmpnt-state wombat-id)]]]]))})))
