(ns wombats_web_client.db
    (:require [wombats_web_client.services.utils :refer [get-item]]))

(def default-db
  {:active-panel nil
   :auth-token (get-item "token")
   :bootstrapping? false
   :name "WillowTree"
   :user nil
   :users []})
