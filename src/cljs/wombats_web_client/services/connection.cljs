(ns wombats-web-client.services.connection
  "Handles setting up the WebSocket connection"
  (:require [wombats-web-client.constants.urls :refer [game-url]]
            [wombats-web-client.utils.socket :as socket]))

(defn connect
  []

  (socket/connect game-url)

  (socket/onopen
    (fn []
      (js/console.log "Socket connected!")))

  (socket/onmessage
    (fn [message]
      (js/console.log "Message receieved")))

  (socket/onerror
    (fn []
      (js/console.log "Error connecting to socket! :("))))