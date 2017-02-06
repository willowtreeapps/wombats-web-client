(ns wombats-web-client.panels.game-play
  (:require [wombats-web-client.components.arena :as arena]
            [wombats-web-client.components.chat-box :refer [chat-box]]
            [wombats-web-client.components.game-ranking :refer [ranking-box]]
            [re-frame.core :as re-frame]
            [reagent.core :as reagent]))

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

(defn update-arena [arena]
  (arena/arena @arena canvas-id))

(defn get-arena-dimensions
  []
  600)

(defn clear-game-state []
   (re-frame/dispatch [:game/update-frame nil])
   (re-frame/dispatch [:game/clear-chat-messages])
   (re-frame/dispatch [:game/stats-update {}]))

(defn game-play []
  (let [game-id (get-game-id)
        messages (re-frame/subscribe [:game/messages])
        arena (re-frame/subscribe [:game/arena])
        stats (re-frame/subscribe [:game/stats])
        dimensions (get-arena-dimensions)]
    (reagent/create-class
     {:component-will-unmount #(clear-game-state)
      :display-name "game-play-panel"
      :reagent-render
      (fn []
        (update-arena arena)
        [:div {:class-name "game-play-panel"}
         [:div {:style {:color "white"}
                :id "wombat-arena"
                :class-name "left-game-play-panel"}
          [:canvas {:id canvas-id
                    :width dimensions
                    :height dimensions}]]
         [:div {:class-name "right-game-play-panel"}
          [ranking-box game-id stats]
          [chat-box game-id messages]]])})))
