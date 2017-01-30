(ns wombats-web-client.services.user
  (:require [ajax.core :refer [GET POST]]
            [ajax.edn :refer [edn-request-format]]
            [wombats-web-client.utils.auth :refer [add-auth-header]]
            [wombats-web-client.constants.urls :refer [self-url 
                                                       github-signout-url 
                                                       my-wombats-url]]))

(defn get-current-user
  "fetches the current user object"
  [on-success on-error]
  (GET self-url {:response-format :json
                 :keywords? true
                 :headers (add-auth-header {})
                 :handler on-success
                 :error-handler on-error}))

(defn post-new-wombat
  "creates and returns a wombat"
  [id name url on-success on-error]
  (POST (my-wombats-url id) {:response-format :json
                                  :format (edn-request-format)
                                :keywords? true
                                :headers (add-auth-header {})
                                :handler on-success
                                :params {:wombat/name name :wombat/url url}
                                :error-handler on-error}))

(defn load-wombats
  "loads all wombats associated with user id"
  [id on-success on-error]
  (GET (my-wombats-url id) {:response-format :json
                              :keywords? true
                              :headers (add-auth-header {})
                              :handler on-success
                              :error-handler on-error}))
(defn sign-out-user
  "signs out user from server and removes their auth token"
  [on-success on-error]
  (GET github-signout-url {:response-format :json
                           :keywords? true
                           :headers (add-auth-header {})
                           :handler on-success
                           :error-handler on-error}))

