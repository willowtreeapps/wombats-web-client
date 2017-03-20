(ns wombats-web-client.components.game-ranking
  (:require [wombats-web-client.utils.games
             :refer [get-wombat-in-game]]))

(defn get-health-color [hp]
  (cond
   (<= 70 hp) "healthy"
   (<= 30 hp 69) "okay-health"
   (<= 1 hp 29) "dying"
   :else "none"))

(defn get-adjusted-hp [game]
  (let [game-status (:game/status game)
        is-starting (or (= game-status :pending-open)
                        (= game-status :pending-closed)
                        (= game-status :active-intermission))]
    (if is-starting 100 0)))

(defn get-bounded-hp [hp]
  (if (> hp 100) 100
      (if (neg? hp) 0 hp)))

(defn- get-player-hp
  [game player-color]
  (let [wombat (get-wombat-in-game game player-color)]
    (get-in wombat [:contents :hp])))

(defn render-wombat-status [game player]
  (let [{:keys [:player/id
                :player/user
                :player/wombat
                :player/stats
                :player/color]} player
        hp (get-player-hp game color)
        adjusted-hp-value (if (nil? hp)
                            (get-adjusted-hp game)
                            (get-bounded-hp hp))
        is-dead (zero? adjusted-hp-value)
        health-color (get-health-color adjusted-hp-value)
        health-percent (str adjusted-hp-value "%")]
    ^{:key id}
    [:li.wombat-status {:class (when is-dead "disabled")}
     [:div.health-bar
      [:span.filling {:class health-color
                      :style
                      {:width health-percent}}]]
     [:img.wombat-img {:src
                       (str "/images/wombat_" color "_right.png")}]
     [:div.wombat-name (:wombat/name wombat)]
     [:div.username (:user/github-username user)]
     [:div.score (:stats/score stats)]]))

(defn ranking-box
  [game]
  (let [game @game
        players (:game/players game)]
    [:div.game-ranking-box
     [:ul.list-wombat-status
      (doall (map #(render-wombat-status game %)
                  players))]]))
