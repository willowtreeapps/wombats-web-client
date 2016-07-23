(ns wombats_web_client.db
    (:require [wombats_web_client.services.utils :refer [get-item]]))

(def default-db
  {:bootstrapping? false
   :auth-token (get-item "token")
   :name "WillowTree"
   :user nil
   :users []})
