(ns wombats-web-client.components.modals.add-wombat-modal
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.events.user :refer [create-new-wombat]]))

(def wombat-name (reagent/atom ""))
(def wombat-url (reagent/atom ""))
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
      [:label {:for "wombat-name"} "Bot Name"]
      [:input {:type "text"
               :name "wombat-name"
               :on-change #(reset! wombat-name (-> % .-target .-value))}]
      [:label {:for "wombat-url"} "Wombat URL"]
      [:input {:type "text"
               :name "wombat-url"
               :on-change #(reset! wombat-url (-> % .-target .-value))}]
      [:input {:type "button"
               :value "Cancel"
               :on-click (fn [] (re-frame/dispatch [:set-modal nil]))}]
      [:input {:type "button"
               :value "Add"
               :on-click #(create-new-wombat 
                           @wombat-name 
                           @wombat-url 
                           callback-success 
                           callback-error)}]]]))
