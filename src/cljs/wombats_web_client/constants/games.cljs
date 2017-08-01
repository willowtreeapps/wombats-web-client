(ns wombats-web-client.constants.games)

(defonce pending-open "pending-open")
(defonce pending-closed "pending-closed")
(defonce active-intermission "active-intermission")
(defonce active "active")
(defonce closed "closed")

(defonce game-type-str-map {:high-score "High Score"})
;; This is how long "ROUND x OVER" will show for (between rounds)
(defonce transition-time 3000)
(defonce simulator-frame-time 1000)
