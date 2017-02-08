(ns wombats-web-client.utils.auth
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.utils.local-storage :refer [get-item]]
            [wombats-web-client.constants.local-storage :refer [token]]))

(defn get-auth-header
  "returns an Authorization header if one should be present"
  []
  {:Authorization (get-item token)})

(defn add-auth-header
  "Add token to header"
  [headers]
  (merge (get-auth-header) headers))

(defn get-current-user-id []
  (let [current-user (re-frame/subscribe [:current-user])]
    (@current-user :id)))

(comment
  ;; could be written as follow, but thats more style preference
  (defn get-current-user-id []
    (:id @(re-frame/subscribe [:current-user]))))
