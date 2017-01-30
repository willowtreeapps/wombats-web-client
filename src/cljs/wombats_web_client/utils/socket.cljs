(ns wombats-web-client.utils.socket
  "Handles connecting to a web socket"
  (:require [cljs.reader :as reader]))

(defonce socket (atom nil))

(defn- parse
  "Parses a string into its proper structure"
  [string]
  (reader/read-string string))

(defn- stringify
  "Converts an object into a string"
  [object]
  (prn-str object))

(defn connect
  "Setup the event listener"
  [url]
  (reset! socket (new js/WebSocket url)))

(defn onopen
  "Set a callback for when the socket is opened"
  [callback]
  (let [chan @socket]
    (set! (.-onopen chan) callback)))

(defn onerror
  "Set a callback for when the socket receives an error"
  [callback]
  (let [chan @socket]
    (set! (.-onerror chan) callback)))

(defn onmessage
  "Set a callback for when the socket receives a message"
  [callback]
  (let [chan @socket]
    (set! (.-onmessage chan) callback)))

(defn send
  "Send data through the socket"
  [data]
  (let [chan @socket]
    (.send chan (stringify data))))