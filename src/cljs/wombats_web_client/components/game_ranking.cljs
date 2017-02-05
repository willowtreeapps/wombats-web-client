(ns wombats-web-client.components.game-ranking)

(defn ranking-box
  [game-id stats]
  (fn []
    [:div {:class-name "game-ranking-box"}
     [:ul
      (for [[player-id {:keys [wombat-name
                               username
                               score
                               hp]}] @stats]
        ^{:key player-id} [:li
                           [:div.meter
                            [:span {:style {:width (str (* (/ hp 100) 100) "%")}}]]
                           [:span wombat-name]
                           [:span username]
                           [:span score]])]]))
