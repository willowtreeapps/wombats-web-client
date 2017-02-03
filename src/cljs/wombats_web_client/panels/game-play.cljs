(ns wombats-web-client.panels.game-play
  (:require [wombats-web-client.components.arena :as arena]
            [re-frame.core :as re-frame]))

(defonce canvas-id "arena-canvas")

(defn update-arena []
  (let [arena (re-frame/subscribe [:game/arena])]
    (arena/arena @arena canvas-id)))

(defn game-play []
  (update-arena)
  [:div {:style {:color "white"}
         :id "wombat-arena"}
   [:canvas {:id canvas-id
             :width 500
             :height 500}]])
