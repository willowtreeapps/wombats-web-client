(ns wombats-web-client.utils.local-storage
  (:require [wombats-web-client.constants.local-storage :refer [token]]))

;;
;; Local Storage
;;
;; https://gist.github.com/daveliepmann/cf923140702c8b1de301
(defn set-token!
  "Set `' in browser's localStorage to `val`."
  [val]
  (.setItem (.-localStorage js/window) token val))

(defn get-token
  "Returns value of `token` from browser's localStorage."
  []
  (.getItem (.-localStorage js/window) token))

(defn remove-token!
  "Remove the browser's localStorage value for the given token"
  []
  (.removeItem (.-localStorage js/window) token))
