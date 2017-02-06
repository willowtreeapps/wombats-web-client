(ns wombats-web-client.components.game-ranking)

(defn ranking-box
  [game-id stats]
  (fn []
    [:div {:class-name "game-ranking-box"}
     [:ul
      (for [{:keys [wombat-name
                    username
                    score
                    hp
                    color]} @stats]
        ^{:key username} [:li
                          [:progress.health-bar {:max 100
                                       :value hp}]
                          [:div.img-wrapper 
                           [:img {:src (str "/images/wombats/wombat_" color "_right.png")}]]
                          [:div.wombat-name wombat-name]
                          [:div.username username]
                          [:div.score score]])]]))



