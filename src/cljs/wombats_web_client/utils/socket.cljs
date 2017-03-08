(ns wombats-web-client.utils.socket
  "Handles connecting to a web socket"
  (:require [cljs.core.async :as async]
            [cljs.reader :as reader]
            [re-frame.core :as re-frame]
            [wombats-web-client.utils.local-storage :refer [get-token]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def default-socket-state {:socket nil
                           :chan-id nil
                           :access-token nil})

(defonce socket-state (atom default-socket-state))

(defn- parse
  "Parses a string into its proper structure"
  [string]
  (reader/read-string string))

(defn disconnect
  []
  (.close (:socket @socket-state))
  (swap! socket-state merge {:socket nil
                             :chan-id nil}))

(defn init
  "Setup the event listener"
  [url]

  (when (:socket @socket-state)
    (disconnect))

  (swap! socket-state merge {:socket (new js/WebSocket url)
                             :chan-id nil}))

(defn onopen
  "Set a callback for when the socket is opened"
  [callback]
  (set! (.-onopen (:socket @socket-state)) callback))

(defn onerror
  "Set a callback for when the socket receives an error"
  [callback]
  (set! (.-onerror (:socket @socket-state)) callback))

(defn onclose
  "Set a callback for when the socket closes"
  [callback]
  (set! (.-onclose (:socket @socket-state)) callback))

(defn send-message
  "Send data through the socket"
  [message-type data]

  (let [{socket :socket
         chan-id :chan-id
         access-token :access-token} @socket-state]

    (.send socket (prn-str {:meta {:msg-type message-type
                                   :chan-id chan-id
                                   :access-token access-token}
                            :payload data}))))

(defn- bootstrap
  [chan-id connection-ch]
  (let [token (get-token)]
    (swap! socket-state assoc :access-token token :chan-id chan-id)
    (send-message :handshake {:chan-id chan-id})
    (send-message :authenticate-user {:access-token token})

    ;; Resolve the channel
    (go
      (async/>! connection-ch true))))

(defn onmessage
  "Set a callback for when the socket receives a message.
  On 'handshake', complete handoff"
  [callback connection-ch]
  (let [onmessage-interceptor (fn [message]
                                (let [formatted-message (parse message.data)
                                      {:keys [meta payload]} formatted-message
                                      message-type (:msg-type meta)
                                      is-handshake? (= message-type :handshake)]
                                  (if is-handshake?
                                    (bootstrap (:chan-id payload) connection-ch)
                                    (callback message-type payload meta))))]

    (set! (.-onmessage (:socket @socket-state)) onmessage-interceptor)))

(defn connect
  [event-bus url connection-ch]

  (init url)

  (onopen
   (fn [] (event-bus send-message :connect "Connected")))

  (onmessage
   (fn [message-type payload metadata]
     (event-bus send-message message-type payload metadata))
   connection-ch)

  (onerror
   (fn [error] (event-bus send-message :error error)))

  (onclose
   (fn [code]
     ;; TODO Add logging
     ))

  (let [socket (:socket @socket-state)]
    (set! (.-gameSocket js/window) socket)
    socket))
