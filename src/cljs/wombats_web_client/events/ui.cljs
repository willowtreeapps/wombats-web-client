(ns wombats-web-client.events.ui
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-event-db
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(re-frame/reg-event-db
 :set-modal
 (fn [db [_ modal]]
   (assoc db :modal modal)))

(re-frame/reg-event-db
 :update-modal-error
 (fn [db [_ modal-error]]
   (assoc db :modal-error modal-error)))
