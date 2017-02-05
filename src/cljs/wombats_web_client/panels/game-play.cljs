(ns wombats-web-client.panels.game-play
  (:require [wombats-web-client.components.arena :as arena]
            [wombats-web-client.constants.chat-box :refer [chat-box]]
            [re-frame.core :as re-frame]))

(defonce canvas-id "arena-canvas")

(defn get-game-id
  "Returns the current game-id

   TODO There is something wrong with route state.
        When trying to use secretary's locate-route-value
        we receive an error and accessing secretary's
        internal *route* state directly returns a bunch of
        null values"
  []
  (last
   (.split (-> js/window .-location .-hash) "/")))

(defn update-arena []
  (let [arena (re-frame/subscribe [:game/arena])]
    (arena/arena @arena canvas-id)))

(defn game-play
  []
  (update-arena)
  (fn []
    (let [game-id (get-game-id)
          messages (re-frame/subscribe [:game/messages])]
      [:div {:style {:color "white"}
             :id "wombat-arena"}
       [:canvas {:id canvas-id
                 :width 500
                 :height 500}]
       [chat-box game-id messages]])))
