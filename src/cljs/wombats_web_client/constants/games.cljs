(ns wombats-web-client.constants.games)

(defonce pending-open "pending-open")
(defonce pending-closed "pending-closed")
(defonce active-intermission "active-intermission")
(defonce active "active")
(defonce closed "closed")

(defn build-status-query [statuses]
  (clojure.string/join "&status=" statuses))
