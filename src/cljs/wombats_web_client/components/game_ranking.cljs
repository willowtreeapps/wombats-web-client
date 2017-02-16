(ns wombats-web-client.components.game-ranking)

(defn get-health-color [hp]
  (cond
   (<= 70 hp 100) "healthy"
   (<= 30 hp 69) "okay-health"
   (<= 1 hp 29) "dying"
   :else "none"))

(defn ranking-box
  [game-id stats]
  (fn []
    [:div {:class-name "game-ranking-box"}
     [:ul.list-wombat-status
      (for [{:keys [wombat-name
                    username
                    score
                    hp
                    color]} @stats]
        ^{:key username} [:li.wombat-status {:class (when (= hp 0) "disabled")}
                          [:div.health-bar
                           [:span.filling {:class (get-health-color hp)
                                           :style {:width (str hp "%")}}]]
                          [:div.img-wrapper
                           [:img {:src (str "/images/wombat_" color "_right.png")}]]
                          [:div.wombat-name wombat-name]
                          [:div.username username]
                          [:div.score score]])]]))



