(ns wombats-web-client.components.modals.add-wombat-modal
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.events.user :refer [create-new-wombat]]
            [wombats-web-client.utils.forms :refer [text-input-with-label
                                                    cancel-modal-input]]))

(def wombat-name (reagent/atom nil))
(def wombat-url (reagent/atom nil))
(def error (reagent/atom nil))

(defn callback-success []
  "closes modal on success"
  (reset! error nil)
  (re-frame/dispatch [:set-modal nil]))

(defn callback-error []
  "says error, persists modal"
  (reset! error "ERROR"))

(defn add-wombat-modal []
  (fn []
    [:div {:class "modal add-wombat-modal"}
     [:div.title "ADD WOMBAT"]
     (if (not (nil? @error)) [:div @error])
     [:form
      [text-input-with-label {:name "wombat-name" 
                              :label "Wombat Name"
                              :local-state-value wombat-name}]
      [text-input-with-label {:name "wombat-url"
                              :label "Wombat URL"
                              :local-state-value wombat-url}]
      [:div.action-buttons
       [cancel-modal-input]
       [:input.modal-button {:type "button"
                             :value "ADD"
                             :on-click #(create-new-wombat 
                                         @wombat-name 
                                         @wombat-url 
                                         callback-success 
                                         callback-error)}]]]]))
