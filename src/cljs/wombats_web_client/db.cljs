(ns wombats_web_client.db
    (:require [wombats_web_client.services.utils :refer [get-item]]))

(def default-db
  {:active-panel {:panel nil :meta {}}
   :auth-token (get-item "token")
   :bootstrapping? false
   :games []
   :name "WillowTree"
   :user nil
   :users []})
