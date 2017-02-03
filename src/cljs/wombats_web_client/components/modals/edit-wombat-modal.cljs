(ns wombats-web-client.components.modals.edit-wombat-modal
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.events.user :refer [edit-wombat-by-id]]
            [wombats-web-client.utils.forms :refer [text-input-with-label
                                                    cancel-modal-input]]))

(def error (reagent/atom nil))

(defn reset-state []
  (reset! error nil))

(defn callback-success []
  "closes modal on success"
  (reset! error nil)
  (re-frame/dispatch [:set-modal nil]))

(defn callback-error []
  "says error, persists modal"
  (reset! error "ERROR"))


(defn edit-wombat-modal [wombat-id name url]
  (fn []
    (let [wombat-name (reagent/atom name)
          wombat-url (reagent/atom url)]
      [:div {:class "modal edit-wombat-modal"}
       [:div.title "EDIT WOMBAT"]
       (if (not (nil? @error)) [:div @error])
       [:form
        [text-input-with-label {:name "wombat-name"
                                :label "Wombat Name"
                                :local-state-value wombat-name}]
        [text-input-with-label {:name "wombat-url"
                                :label "Wombat URL"
                                :local-state-value wombat-url}]
        [:div.action-buttons
         [cancel-modal-input reset-state]
         [:input.modal-button {:type "button"
                               :value "SAVE"
                               :on-click #(edit-wombat-by-id
                                           @wombat-name
                                           @wombat-url
                                           wombat-id
                                           callback-success
                                           callback-error)}]]]])))
