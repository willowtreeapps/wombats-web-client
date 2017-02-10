(ns wombats-web-client.events.spritesheet
  (:require [re-frame.core :as re-frame]
            [ajax.core :refer [GET]]
            [wombats-web-client.constants.urls :refer [spritesheet-url]]))

(defn get-spritesheet-request 
  "Fetches the spritesheet json"
  [on-success on-error]
  (GET spritesheet-url {:response-format :json
                        :keywords? true
                        :handler on-success
                        :error-handler on-error}))

(defn get-spritesheet []
  (get-spritesheet-request
   #(re-frame/dispatch [:update-spritesheet %])
   #(print "error with get-spritesheet-request")))

(re-frame/reg-event-db
 :update-spritesheet
 (fn [db [_ spritesheet]]
   (assoc db :spritesheet (:frames spritesheet))))

(re-frame/reg-event-fx
 :get-spritesheet
 (fn [_] 
   (get-spritesheet)
   nil))
