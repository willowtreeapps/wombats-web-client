(ns wombats-web-client.utils.games)

(defn build-status-query [statuses]
  (clojure.string/join "&status=" statuses))
