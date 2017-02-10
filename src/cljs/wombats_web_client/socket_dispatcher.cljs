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
          :stats-update #(re-frame/dispatch [:game/stats-update payload])
          :disconnect #()
          :error #()
          #())]
    (event-fn)))

(defn init-ws-connection []
  (let [socket (ws/connect message-bus urls/ws-url)]
    (set! (.-gameSocket js/window) socket)))

(defn- socket-polling
  "Whenever a socket connection is lost, we want to catch that event and
  reboot the connection process"
  []
  (js/setInterval
   (fn []
     (let [socket (-> js/window .-gameSocket)
           ready-state (.-readyState socket)]
       (when-not (= ready-state 1)
         (init-ws-connection)))) 2000))

;; Starts the socket polling process
(socket-polling)
