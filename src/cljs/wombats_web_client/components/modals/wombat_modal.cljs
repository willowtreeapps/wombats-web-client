(ns wombats-web-client.components.modals.wombat-modal
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.components.text-input
             :refer [text-input-with-label]]
            [wombats-web-client.components.select-input
             :refer [select-input]]
            [wombats-web-client.events.user :refer [create-new-wombat
                                                    edit-wombat-by-id
                                                    get-all-repositories]]
            [wombats-web-client.utils.errors :refer [required-field-error]]
            [wombats-web-client.utils.forms :refer [cancel-modal-input
                                                    submit-modal-input
                                                    optionize]]))

(defn callback-success [state]
  "closes modal on success"
  (re-frame/dispatch [:update-modal-error nil])
  (re-frame/dispatch [:set-modal nil]))

(defn on-submit-form-valid [cmpnt-state username wombat-id]
  (let [{:keys [wombat-name
                wombat-repo-name
                wombat-file-path]} @cmpnt-state
        url (str username "/"
                 wombat-repo-name "/contents/"
                 wombat-file-path)]
    (when (clojure.string/blank? wombat-name)
      (swap! cmpnt-state assoc :wombat-name-error required-field-error))
    (when (clojure.string/blank? wombat-repo-name)
      (swap! cmpnt-state assoc :wombat-repo-name-error required-field-error))
    (when (clojure.string/blank? wombat-file-path)
      (swap! cmpnt-state assoc :wombat-file-path-error required-field-error))

    (when (and wombat-name wombat-repo-name wombat-file-path)
      (if wombat-id
        (edit-wombat-by-id
         wombat-name
         url
         wombat-id
         #(callback-success cmpnt-state))
        (create-new-wombat wombat-name url #(callback-success cmpnt-state))))))

(defn parse-url [url key]
  (cond
   (= key "repo-name") (get (clojure.string.split url "/") 1)
   (= key "file-path") (last (clojure.string.split url "/"))))

(defn wombat-modal [{:keys [wombat-id name url]}]
  (let [repo-name (parse-url url "repo-name")
        file-path (parse-url url "file-path")
        submit-text (if wombat-id "SAVE" "ADD")
        title (if wombat-id "EDIT WOMBAT" "ADD WOMBAT")
        cmpnt-state (reagent/atom {:wombat-name name
                                   :wombat-repo-name repo-name
                                   :wombat-file-path file-path
                                   :wombat-name-error nil
                                   :wombat-repo-name-error nil
                                   :wombat-file-path-error nil
                                   :error nil})
        modal-error (re-frame/subscribe [:modal-error])
        current-user (re-frame/subscribe [:current-user])]
    (reagent/create-class
     {:component-will-unmount #(re-frame/dispatch [:update-modal-error nil])
      :display-name "wombat-modal"
      :reagent-render
      (fn []
        (let [{:keys [wombat-name
                      wombat-repo-name
                      wombat-file-path]} @cmpnt-state
                      error @modal-error
              repositories @(re-frame/subscribe [:my-repositories])
                      username (:user/github-username @current-user)
              repository-options (optionize [:name]
                                            [:name]
                                            repositories)]
          (prn repository-options)
          [:div {:class "modal add-wombat-modal"}
           [:div.title title]
           (when error [:div.modal-error error])
           [:div.modal-content
            [text-input-with-label {:name "wombat-name"
                                    :label "Wombat Name"
                                    :state cmpnt-state}]
            [select-input {:id "repository-dropdown"
                           :name "wombat-repo-name"
                           :form-state cmpnt-state
                           :form-key :wombat-repo-name
                           :error-key :wombat-repo-name-error
                           :option-list repository-options
                           :label "Wombat Repository"
                           :disabled (some? wombat-id)}]


            [text-input-with-label {:name "wombat-file-path"
                                    :label "Wombat File Path"
                                    :state cmpnt-state
                                    :disabled (some? wombat-id)}]

            ]
           (prn cmpnt-state)
           [:div.action-buttons
            [cancel-modal-input]
            [submit-modal-input
             submit-text
             #(on-submit-form-valid
               cmpnt-state
               username
               wombat-id)]]]))})))

#_[text-input-with-label {:name "wombat-repo-name"
                                    :label "Wombat Repository Name"
                                    :state cmpnt-state
                                    :disabled (some? wombat-id)}]
