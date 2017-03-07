(ns wombats-web-client.utils.local-storage
  (:require [wombats-web-client.constants.local-storage :refer [token]]))

;;
;; Local Storage
;;
;; https://gist.github.com/daveliepmann/cf923140702c8b1de301
(defn set-item! [key value]
  (.setItem (.-localStorage js/window) key value))

(defn set-token!
  "Set token in browser's localStorage to `val`."
  [val]
  (set-item! token val))

(defn get-item 
  "Returns value of key from browser's localStorage"
  [key]
  (.getItem (.-localStorage js/window) key))

(defn get-token
  "Returns value of `token` from browser's localStorage."
  []
  (get-item token))

(defn remove-item!
  "Remove the browser's localStorage value for the given key"
  [key]
  (.removeItem (.-localStorage js/window) key))

(defn remove-token!
  "Remove the browser's localStorage value for the given key"
  []
  (remove-item! token))
