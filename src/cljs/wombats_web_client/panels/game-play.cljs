(ns wombats-web-client.panels.game-play
  (:require [wombats-web-client.components.arena :as arena]
            [wombats-web-client.components.cards.game :refer [open-join-game-modal-fn]]
            [wombats-web-client.components.chat-box :refer [chat-box]]
            [wombats-web-client.components.countdown-timer :refer [countdown-timer]]
            [wombats-web-client.components.game-ranking :refer [ranking-box]]
            [wombats-web-client.components.modals.wombat-modal :refer [winner-modal]]
            [wombats-web-client.utils.socket :as ws]
            [re-frame.core :as re-frame]
            [reagent.core :as reagent]))

;; This local state can be removed when the id is passed through the router
(defonce canvas-id "arena-canvas")

;; Lifecycle Methods

(defn- component-will-mount [game-id]
  (re-frame/dispatch [:game/join-game game-id]))

(defn component-will-unmount [game-id]
  (re-frame/dispatch [:game/update-frame nil])
  (re-frame/dispatch [:game/clear-chat-messages])
  (re-frame/dispatch [:game/info nil])
  (ws/send-message :leave-game {:game-id game-id})
  (re-frame/dispatch [:set-modal nil]))

(defn update-arena [arena]
  (arena/arena @arena canvas-id))

(defn get-arena-dimensions []
  600)


(defn- game-play-title [info show-join-button game-id]
  (let [{:keys [round-number
                round-start-time
                status]} @info]
    [:div.game-play-title-container
     
     [:h1.game-play-title
      (case status
        :closed
        "GAME OVER"

        (:pending-open
         :pending-closed
         :active-intermission)
        [:span (str "ROUND " round-number " STARTS IN: ")
         [countdown-timer round-start-time]]

        :active
        (str "ROUND " round-number)

        nil)]

     (when (and show-join-button (= status :pending-open))
       [:button.join-button 
        {:class (when false "private")
         :on-click (open-join-game-modal-fn game-id)}
        "JOIN"])]))

(defn- game-play-subtitle [info]
  (let [{:keys [name]} @info]
    [:h2.game-play-subtitle
     (when name
       (str name " - High Score"))]))

(defn- max-players [info]
  (let [{:keys [max-players stats]} @info
        player-count (count stats)]
    [:p.wombat-counter (when (and max-players stats)
                         (str "Wombats: " player-count "/" max-players))]))

(defn chat-title []
  [:div.chat-title
   [:span "CHAT"]])

(defn- show-winner-modal
  [winner]
  (re-frame/dispatch [:set-modal {:fn #(winner-modal winner)
                                  :show-overlay? false}]))

(defn right-game-play-panel
  [info messages user game-id]

  (let [{:keys [game-winner stats]} @info
        user-bots (filter #(= (:username %)
                              (::user/github-username @user))
                          stats)]
    ;; Dispatch winner modal if there's a winner
    (when game-winner
      (show-winner-modal game-winner))

    [:div.right-game-play-panel

     [:div.top-panel
      [game-play-title info (= 0 (count user-bots)) game-id]
      [game-play-subtitle info]
      [max-players info]
      [ranking-box info]]

     (when (> (count user-bots) 0)
       [:div.chat-panel
        [chat-title]
        [chat-box game-id messages info]])]))

(defn game-play [{:keys [game-id]}]
  (let [dimensions (get-arena-dimensions)
        arena (re-frame/subscribe [:game/arena])
        info (re-frame/subscribe [:game/info])
        messages (re-frame/subscribe [:game/messages])
        user (re-frame/subscribe [:current-user])]

    (reagent/create-class
     {:component-will-mount #(component-will-mount game-id)
      :component-will-unmount #(component-will-unmount game-id)
      :display-name "game-play-panel"
      :reagent-render
      (fn []
        (let [winner (:game-winner @info)]
          (update-arena arena)
          [:div {:class-name "game-play-panel"}
           [:div.left-game-play-panel {:id "wombat-arena"
                  :class (when winner "game-over")}
            [:canvas {:id canvas-id
                      :width dimensions
                      :height dimensions}]]
           [right-game-play-panel info messages user game-id]]))})))
