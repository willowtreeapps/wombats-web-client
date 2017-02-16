(ns wombats-web-client.components.game-ranking
  (:require [wombats-web-client.constants.colors :refer [wombat-green
                                                         orange-yellow
                                                         red-dying
                                                         transparent]]))

(defn get-health-color [hp]
  (cond
   (<= 70 hp 100) wombat-green
   (<= 30 hp 69) orange-yellow
   (<= 1 hp 29) red-dying
   :else transparent))

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
                           [:span.filling {:style {:width (str hp "%")
                                                   :background-color (get-health-color hp)}}]]
                          [:div.img-wrapper
                           [:img {:src (str "/images/wombat_" color "_right.png")}]]
                          [:div.wombat-name wombat-name]
                          [:div.username username]
                          [:div.score score]])]]))



