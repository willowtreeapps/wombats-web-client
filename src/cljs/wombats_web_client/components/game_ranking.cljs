(ns wombats-web-client.components.game-ranking)

(defn get-health-color [hp]
  (cond
   (<= 70 hp 100) "healthy"
   (<= 30 hp 69) "okay-health"
   (<= 1 hp 29) "dying"
   :else "none"))

(defn get-adjusted-hp [isStarting?]
  (if isStarting? 100 0))

(defn ranking-box
  [game-id stats info]
  (fn []
    (let [game-status (:status @info)
          isStarting? (or (= game-status :pending-open) (= game-status :pending-closed))]
      [:div {:class-name "game-ranking-box"}
       [:ul.list-wombat-status
        (for [{:keys [wombat-name
                      username
                      score
                      hp
                      color]} @stats]
          (let [hpNil? (nil? hp)
                adjusted-hp-value (if hpNil? (get-adjusted-hp isStarting?) hp)]
            ^{:key username} [:li.wombat-status {:class (when (= adjusted-hp-value 0) "disabled")}
                              [:div.health-bar
                               [:span.filling {:class (get-health-color adjusted-hp-value)
                                               :style {:width (str adjusted-hp-value "%")}}]]
                              [:div.img-wrapper
                               [:img {:src (str "/images/wombat_" color "_right.png")}]]
                              [:div.wombat-name wombat-name]
                              [:div.username username]
                              [:div.score score]]))]])))



