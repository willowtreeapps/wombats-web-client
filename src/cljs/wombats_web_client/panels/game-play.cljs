(ns wombats-web-client.panels.game-play
  (:require [wombats-web-client.components.arena :as arena]
            [re-frame.core :as re-frame]))

(defn game-play []
  (let [arena (re-frame/subscribe [:game/arena])]
    [:div {:style {:color "white"}
           :id "wombat-arena"}
     [arena/arena @arena]]))
