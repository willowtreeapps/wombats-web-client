(ns wombats-web-client.db
  (:require [wombats-web-client.constants.local-storage :refer [token]]
            [wombats-web-client.utils.local-storage :refer [get-item]]))

(def default-db
  {:active-panel nil
   :auth-token (get-item token)
   :bootstrapping? false
   :name "WillowTree"
   :current-user nil
   :users []
   :modal nil
   :open-games nil
   :my-games nil
   :game/arena nil
   :game/messages []})
