(ns wombats-web-client.services.user
  (:require [ajax.core :refer [GET]]
            [wombats-web-client.utils.auth :refer [add-auth-header]]))

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
