(ns wombats-web-client.components.game-ranking)

(defn get-health-color [hp]
  (cond
   (<= 70 hp 100) "healthy"
   (<= 30 hp 69) "okay-health"
   (<= 1 hp 29) "dying"
   :else "none"))

(defn get-adjusted-hp [info]
  (let [game-status (:status info)
        is-starting? (or (= game-status :pending-open) (= game-status :pending-closed) (= game-status :active-intermission))]
    (if is-starting? 100 0)))

(defn render-wombat-status [info stat]
  (let [{:keys [wombat-name
                username
                score
                hp
                color]} stat
        adjusted-hp-value (if (nil? hp) (get-adjusted-hp info) hp)]
    ^{:key username} [:li.wombat-status {:class (when (= adjusted-hp-value 0) "disabled")}
                            [:div.health-bar
                             [:span.filling {:class (get-health-color adjusted-hp-value)
                                             :style {:width (str adjusted-hp-value "%")}}]]
                            [:img.wombat-img {:src (str "/images/wombat_" color "_right.png")}]
                            [:div.wombat-name wombat-name]
                            [:div.username username]
                            [:div.score score]]))
(defn ranking-box
  [info]
  (fn []
    (let [info @info
          stats (:stats info)]
      [:div {:class-name "game-ranking-box"}
       [:ul.list-wombat-status
        (doall (map #(render-wombat-status info %) stats))]])))



