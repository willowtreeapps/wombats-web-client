(ns wombats-web-client.panels.game-play
  (:require [wombats-web-client.components.arena :as arena]
            [wombats-web-client.components.chat-box :refer [chat-box]]
            [wombats-web-client.components.game-ranking :refer [ranking-box]]
            [wombats-web-client.utils.socket :as ws]
            [re-frame.core :as re-frame]
            [reagent.core :as reagent]))

;; This local state can be removed when the id is passed through the router
(defonce game-id (atom nil))
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
  (re-frame/dispatch [:game/stats-update {}])
  (ws/send-message :leave-game {:game-id @game-id})
  (reset! game-id nil))

(defn- game-play-title [info]
  (let [{:keys [status
                start-time]} @info]
    [:h1.game-play-title 
     (case status
       :closed
       "GAME OVER"
           
       ;; TODO: #21 Pull down round number from API and display it
       (:pending-open
        :pending-closed)
       "ROUND 1 STARTS IN: 2:06"

       ;; TODO: Pull down round number from API and display it
       :active
       "ROUND 1"

       nil)]))

(defn- game-play-subtitle [info]
  (let [{:keys [name]} @info]
    [:h2.game-play-subtitle 
     (when name
       (str name " - High Score"))]))

(defn- max-players [info stats]
  (let [{:keys [max-players]} @info
        player-count (count @stats)]
    [:p.wombat-counter (when (and max-players player-count)
                         (str "Wombats: " player-count "/" max-players))]))

(defn chat-title []
  [:div.chat-title 
   [:span "CHAT"]])

(defn right-game-play-panel []
  (let [messages (re-frame/subscribe [:game/messages])
        arena (re-frame/subscribe [:game/arena])
        stats (re-frame/subscribe [:game/stats])
        info (re-frame/subscribe [:game/info])]

    (update-arena arena)
    [:div.right-game-play-panel

     [:div.top-panel
      [game-play-title info]
      [game-play-subtitle info]
      [max-players info stats]
      [ranking-box @game-id stats]]

     [:div.chat-panel
      [chat-title]
      [chat-box @game-id messages stats]]]))

(defn game-play []
  (let [dimensions (get-arena-dimensions)]
    ;; TODO This should come from the router
    (reset! game-id (get-game-id))

    (reagent/create-class
     {:component-will-unmount #(clear-game-state)
      :display-name "game-play-panel"
      :reagent-render
      (fn []
        [:div {:class-name "game-play-panel"}
         [:div {:style {:color "white"}
                :id "wombat-arena"
                :class-name "left-game-play-panel"}
          [:canvas {:id canvas-id
                    :width dimensions
                    :height dimensions}]]
         [right-game-play-panel]])})))
