(ns wombats-web-client.utils.auth
  (:require [wombats-web-client.utils.local-storage :refer [get-item]]))

(defn get-auth-header
  "returns an Authorization header if one should be present"
  []
  {:Authorization (get-item "token")})

(defn add-auth-header
  "Add token to header"
  [headers]
  (merge (get-auth-header) headers))
