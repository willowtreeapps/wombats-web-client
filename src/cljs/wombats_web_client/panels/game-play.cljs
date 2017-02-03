(ns wombats-web-client.panels.game-play
  (:require [wombats-web-client.components.arena :as arena]
            [re-frame.core :as re-frame]))

(defn game-play []
  (let [arena (re-frame/subscribe [:game/arena])
        canvas-id "arena-canvas"]
    (arena/arena @arena canvas-id)
    [:div {:style {:color "white"}
           :id "wombat-arena"}
     [:canvas {:id canvas-id
               :width 500
               :height 500}]]))
