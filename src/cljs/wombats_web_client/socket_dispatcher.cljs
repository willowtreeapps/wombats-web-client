(ns wombats-web-client.socket-dispatcher
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.constants.urls :as urls]
            [wombats-web-client.utils.socket :as ws]))

(defn- add-metadata
  [payload metadata]
  {:payload payload
   :metadata metadata})

(defn- message-bus
  [send-ch msg-type payload metadata]
  (let [event-fn
        (condp = msg-type
          :frame-update #(re-frame/dispatch [:game/update-frame payload])
          :chat-message #(re-frame/dispatch [:game/add-chat-message payload])
          :disconnect #()
          :error #()
          #())]
    (event-fn)))

(defn init-ws-connection []
  (ws/connect message-bus urls/ws-url))
