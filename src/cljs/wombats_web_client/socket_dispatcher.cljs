(ns wombats-web-client.socket-dispatcher
  (:require [cljs.core.async :as async]
            [re-frame.core :as re-frame]
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
          :game-info #(re-frame/dispatch [:game/info payload])
          :disconnect #()
          :error #()
          #())]
    (event-fn)))

(defn init-ws-connection []
  (let [socket-connection-ch (async/chan)
        socket (ws/connect message-bus urls/ws-url socket-connection-ch)]
    socket-connection-ch))

(defn- socket-polling
  "Whenever a socket connection is lost, we want to catch that event and
  reboot the connection process"
  []
  (js/setInterval
   (fn []
     (let [socket (-> js/window .-gameSocket)
           ready-state (.-readyState socket)]

       (if-not (= ready-state 1)
         (init-ws-connection)
         (ws/send-message
          :keep-alive
          {:msg "Whether you're a brother or whether you're a mother, you're stayin' alive, stayin' alive"}))))
   5000))
