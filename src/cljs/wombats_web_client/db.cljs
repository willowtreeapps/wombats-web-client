(ns wombats-web-client.db)

(defn get-item
  "Returns value of `key' from browser's localStorage."
  [key]
  (.getItem (.-localStorage js/window) key))

(def default-db
  {:active-panel nil
   :auth-token (get-item "token")
   :bootstrapping? false
   :name "WillowTree"
   :current-user nil
   :users []})
