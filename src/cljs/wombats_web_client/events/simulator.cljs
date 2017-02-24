(ns wombats-web-client.events.simulator
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.utils.socket :as ws]))

(re-frame/reg-event-db
  :simulator/initialize
  (fn [db [_ payload]]
    ;; TODO: Remove this
    (js/setTimeout #(ws/send-message :connect-to-simulator payload)
                   2000)
    db))

(re-frame/reg-event-db
 :simulator/update-state
 (fn [db [_ state]]
   (assoc db :simulator/state state)))
