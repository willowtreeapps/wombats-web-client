(ns wombats-web-client.handlers
  (:require [ajax.core :refer [GET POST DELETE]]
            [wombats-web-client.services.user :refer [get-current-user, sign-out-user]]))

(defn load-user
  "fetches the current user"
  []
  (get-current-user
   #(re-frame/dispatch [:update-user %]) ; success function, % = payload
   (print "error")))


;; AUTH SPECIFIC
(defn sign-out
  []
  (sign-out-user
   #(re-frame/dispatch [:sign-out %])
   (print "error")))
