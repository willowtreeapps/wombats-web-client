(ns wombats-web-client.events.bootstrap
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-event-db
 :socket-connected
 (fn [db [_ connected]]
   (assoc db :socket/connected connected)))