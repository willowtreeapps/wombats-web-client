(ns wombats-web-client.panels.game-play
  (:require [wombats-web-client.components.arena :as arena]
            [wombats-web-client.components.cards.game :refer [open-join-game-modal-fn]]
            [wombats-web-client.components.chat-box :refer [chat-box]]
            [wombats-web-client.components.countdown-timer :refer [countdown-timer]]
            [wombats-web-client.components.game-ranking :refer [ranking-box]]
            [wombats-web-client.components.join-button :refer [join-button]]
            [wombats-web-client.components.modals.wombat-modal :refer [winner-modal]]
            [wombats-web-client.utils.socket :as ws]
            [re-frame.core :as re-frame]
            [reagent.core :as reagent]))

;; This local state can be removed when the id is passed through the router
(defonce canvas-id "arena-canvas")

;; Lifecycle Methods

(defn- component-will-mount [game-id]
  (ws/send-message :join-game {:game-id game-id}))

(defn- component-will-unmount [game-id]
  (re-frame/dispatch [:game/update-frame nil])
  (re-frame/dispatch [:game/clear-chat-messages])
  (ws/send-message :leave-game {:game-id game-id})
  (re-frame/dispatch [:set-modal nil]))

(defn update-arena [arena]
  (arena/arena @arena canvas-id))

(defn get-arena-dimensions []
  600)

(defn- game-play-title [game show-join-button game-id]
  (let [{:keys [game/is-private
                game/round-number
                game/start-time
                game/status]} game]

    [:div.game-play-title-container

     [:h1.game-play-title
      (case status
        :closed
        "GAME OVER"

        (:pending-open
         :pending-closed
         :active-intermission)
        [:span (str "ROUND " round-number " STARTS IN: ")
         [countdown-timer start-time]]

        :active
        (str "ROUND " round-number)

        nil)]

     (when (and show-join-button (= status :pending-open))
       [join-button {:is-private is-private
                     :on-click (open-join-game-modal-fn game-id)}])]))

(defn- game-play-subtitle [{:keys [game/name]}]
  [:h2.game-play-subtitle
   (when name
     (str name " - High Score"))])

(defn- max-players [{:keys [game/max-players game/stats]}]
  (let [player-count (count stats)]
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
  [game messages user game-id]

  (let [{:keys [game/game-winner game/players]} game
        user-bots-count (count (filter #(= (get-in % [:player/user :user/github-username])
                                           (::user/github-username @user))
                                       players))]

    ;; Dispatch winner modal if there's a winner
    (when game-winner
      (show-winner-modal game-winner))

    [:div.right-game-play-panel

     [:div.top-panel
      [game-play-title game (= 0 user-bots-count) game-id]
      [game-play-subtitle game]
      [max-players game]
      [ranking-box game]]

     (when (> user-bots-count 0)
       [:div.chat-panel
        [chat-title]
        [chat-box game-id messages game]])]))

(defn game-play [{:keys [game-id]}]
  (let [dimensions (get-arena-dimensions)
        arena (re-frame/subscribe [:game/arena])
        messages (re-frame/subscribe [:game/messages])
        user (re-frame/subscribe [:current-user])
        games (re-frame/subscribe [:games])]

    (reagent/create-class
     {:component-will-mount #(component-will-mount game-id)
      :component-will-unmount #(component-will-unmount game-id)
      :display-name "game-play-panel"
      :reagent-render
      (fn []
        (let [game (get @games game-id)
              winner (:game/winner game)]
          (update-arena arena)
          [:div {:class-name "game-play-panel"}
           [:div.left-game-play-panel {:id "wombat-arena"
                                       :class (when winner "game-over")}
            [:canvas {:id canvas-id
                      :width dimensions
                      :height dimensions}]]
           [right-game-play-panel game messages user game-id]]))})))
