(ns wombats-web-client.constants.games)

(defonce pending-open "pending-open")
(defonce pending-closed "pending-closed")
(defonce active-intermission "active-intermission")
(defonce active "active")
(defonce closed "closed")

(defonce open-games-query 
  (str pending-open "&status=" pending-closed "&status=" active "&status=" active-intermission))

