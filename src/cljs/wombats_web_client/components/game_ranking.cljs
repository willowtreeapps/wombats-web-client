(ns wombats-web-client.components.game-ranking)

(defn ranking-box
  [game-id stats]
  (fn []
    [:div {:class-name "game-ranking-box"}
     [:ul
      (for [[player-id {:keys [wombat-name
                               username
                               score]}] @stats]
        ^{:key player-id} [:li
                           [:span wombat-name]
                           [:span username]
                           [:span score]])]]))
