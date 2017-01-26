(ns wombats-web-client.handlers
  (:require [ajax.core :refer [GET POST DELETE]]))
;;
;; Service Helpers
;;


(defn get-item
  "Returns value of `key' from browser's localStorage."
  [key]
  (.getItem (.-localStorage js/window) key))

(defn get-auth-header
  "returns an Authorization header if one should be present"
  []
  {:Authorization (get-item "token")})

(defn add-auth-header
  "Add token to header"
  [headers]
  (merge (get-auth-header) headers))

;;
;; Account
;;
(defn get-current-user
  "fetches the current user object"
  [on-success on-error]
  (GET "http://54.145.152.66/api/v1/self" {:response-format :json
                                               :keywords? true
                                               :headers (add-auth-header {})
                                               :handler on-success
                                               :error-handler on-error}))
(defn sign-out-user
  "signs out user from server and removes their auth token"
  [on-success on-error]
  (GET "http://54.145.152.66/api/v1/auth/github/signout" {:response-format :json
                                               :keywords? true
                                               :headers (add-auth-header {})
                                               :handler on-success
                                           :error-handler on-error}))
