(ns wombats-web-client.events.spritesheet
  (:require [cljs.core.async :as async]
            [re-frame.core :as re-frame]
            [ajax.core :refer [GET]]
            [wombats-web-client.constants.urls :refer [spritesheet-url]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn get-spritesheet
  "Fetches the spritesheet json"
  []
  (let [ch (async/chan)]
    (GET spritesheet-url {:response-format :json
                          :keywords? true
                          :handler #(go (async/>! ch %))
                          :error-handler #(go (async/>! ch nil))})
    ch))

(re-frame/reg-event-db
 :update-spritesheet
 (fn [db [_ spritesheet]]
   (assoc db :spritesheet (:frames spritesheet))))
