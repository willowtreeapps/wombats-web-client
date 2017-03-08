(ns wombats-web-client.panels.game-play
  (:require [wombats-web-client.components.arena :as arena]
            [wombats-web-client.components.cards.game :refer [open-join-game-modal-fn]]
            [wombats-web-client.components.chat-box :refer [chat-box]]
            [wombats-web-client.components.countdown-timer :refer [countdown-timer]]
            [wombats-web-client.components.game-ranking :refer [ranking-box]]
            [wombats-web-client.components.join-button :refer [join-button]]
            [wombats-web-client.components.modals.winner-modal :refer [winner-modal]]
            [wombats-web-client.utils.socket :as ws]
            [re-frame.core :as re-frame]
            [reagent.core :as reagent]))

(defonce root-class "game-play-panel")
(defonce canvas-id "arena-canvas")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Helper Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- resize-canvas []
  (let [root-element (first (array-seq (.getElementsByClassName js/document root-class)))
        canvas-element (.getElementById js/document canvas-id)
        half-width (/ (.-offsetWidth root-element) 2)]

    (set! (.-width canvas-element) half-width)
    (set! (.-height canvas-element) half-width)))

(defn- show-winner-modal
  [winner]
  (re-frame/dispatch [:set-modal {:fn #(winner-modal winner)
                                  :show-overlay? false}]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Lifecycle Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- component-did-mount [cmpnt-state]
  ;; Add resize listener
  (.addEventListener js/window
                     "resize"
                     (:resize-fn @cmpnt-state))
  (resize-canvas))

(defn- component-will-mount [game-id]
  (ws/send-message :join-game {:game-id game-id}))

(defn- component-will-unmount [game-id cmpnt-state]
  (re-frame/dispatch [:game/update-frame nil])
  (re-frame/dispatch [:game/clear-chat-messages])
  (ws/send-message :leave-game {:game-id game-id})
  (re-frame/dispatch [:set-modal nil])

  ;; Remove resize listener
  (.removeEventListener js/window
                        "resize"
                        (:resize-fn @cmpnt-state)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Render Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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

(defn- chat-title []
  [:div.chat-title
   [:span "CHAT"]])

(defn- right-game-play-panel
  [game messages user game-id]

  (let [{:keys [game/winner game/players]} game
        user-bots-count (count (filter #(= (get-in % [:player/user :user/github-username])
                                           (::user/github-username @user))
                                       players))]

    ;; Dispatch winner modal if there's a winner
    (when winner
      (show-winner-modal winner))

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main Method
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn game-play [{:keys [game-id]}]
  (let [arena (re-frame/subscribe [:game/arena])
        cmpnt-state (reagent/atom {:resize-fn #(resize-canvas)})
        messages (re-frame/subscribe [:game/messages])
        user (re-frame/subscribe [:current-user])
        games (re-frame/subscribe [:games])]

    (reagent/create-class
     {:component-did-mount #(component-did-mount cmpnt-state)
      :component-will-mount #(component-will-mount game-id)
      :component-will-unmount #(component-will-unmount game-id cmpnt-state)
      :display-name "game-play-panel"
      :reagent-render
      (fn []
        (let [game (get @games game-id)
              winner (:game/winner game)]
          (arena/arena @arena canvas-id)
          [:div {:class-name root-class}
           [:div.left-game-play-panel {:id "wombat-arena"
                                       :class (when winner "game-over")}
            [:canvas {:id canvas-id}]]
           [right-game-play-panel game messages user game-id]]))})))
