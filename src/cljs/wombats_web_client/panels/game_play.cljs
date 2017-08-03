(ns wombats-web-client.panels.game-play
  (:require [wombats-web-client.components.arena :as arena]
            [wombats-web-client.components.cards.game
             :refer [open-join-game-modal-fn]]
            [wombats-web-client.components.chat-box :refer [chat-box]]
            [wombats-web-client.components.countdown-timer
             :refer [countdown-timer]]
            [wombats-web-client.components.game-ranking :refer [ranking-box]]
            [wombats-web-client.components.join-button :refer [join-button]]
            [wombats-web-client.components.modals.winner-modal
             :refer [winner-modal]]
            [wombats-web-client.constants.games
             :refer [game-type-str-map transition-time]]
            [wombats-web-client.constants.ui :refer [mobile-window-width]]
            [wombats-web-client.utils.games
             :refer [get-player-by-username get-player-score]]
            [wombats-web-client.utils.socket :as ws]
            [wombats-web-client.utils.time :as time]
            [re-frame.core :as re-frame]
            [reagent.core :as reagent]))

(defonce root-class "game-play-panel")
(defonce canvas-id "arena-canvas")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Helper Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- get-ratio
  "Returns an object containing width and
  height as the ratios of the different sides"
  [{:keys [width height]}]
  {:width (if (> height width) (/ width height) 1)
   :height (if (> width height) (/ height width) 1)})


(defn- resize-canvas [arena-atom game]
  (let [root-element (first
                      (array-seq
                       (.getElementsByClassName
                        js/document
                        root-class)))
        dimensions {:width (get-in @game [:game/arena :arena/width])
                    :height (get-in @game [:game/arena :arena/height])}
        arena-ratio (get-ratio dimensions)
        canvas-element (.getElementById js/document canvas-id)
        width (.-offsetWidth root-element)
        half-width (/ width 2)
        height (.-offsetHeight root-element)
        mobile-mode (< width mobile-window-width)
        dimension (if mobile-mode
                    width
                    (min height half-width))]
    ;; Set dimensions of canvas
    (set! (.-width canvas-element) (*  dimension (:width arena-ratio)))
    (set! (.-height canvas-element) (* dimension (:height arena-ratio)))
    (arena/arena @arena-atom canvas-id)))

(defn- on-resize [arena-atom game]
  (resize-canvas arena-atom game)
  (js/setTimeout #(resize-canvas arena-atom game)
                 100))

(defn- show-winner-modal
  ;; Players are always sorted by score
  [players]
  (let [top-score (get-player-score (first players))
        winners (filter #(= (get-player-score %)
                            top-score)
                        players)]
    (re-frame/dispatch [:set-modal {:fn #(winner-modal winners)
                                    :show-overlay true}])))

(defn- get-next-round-text
  [{:keys [:game/round-intermission]
    {:keys [:frame/round-number]} :game/frame}
   millis-left
   timeout-fn]
  ;; The first 3 seconds of a round ending, show transition text
  (let [time-since-round-end (- round-intermission
                                millis-left)
        transition-time-left (- transition-time
                                time-since-round-end)]
    (if (and (> round-number 1)
             (pos? transition-time-left))
      (do
        (timeout-fn transition-time-left)
        (str "ROUND " (dec round-number) " OVER"))
      ;; When we need to transition to show "READY"
      (let [show-ready-ms (- millis-left transition-time)]
        (when (pos? show-ready-ms)
          (timeout-fn show-ready-ms))
        nil))))

(defn- get-transition-text
  [{:keys [:game/start-time
           :game/status]
    {:keys [:frame/round-start-time]} :game/frame
    :as game}
   cmpnt-state]
  (let [millis-left (* (time/seconds-until (or round-start-time
                                               start-time))
                       1000)
        timeout-fn (fn [ms]
                     (js/setTimeout #(swap! cmpnt-state update-in [:update] not)
                                    ms))]

    ;; Force a rerender when the transition-text should change
    (when (contains? #{:active-intermission :pending-open :pending-closed}
                     status)
      (case millis-left
        3000 (do (timeout-fn 1000) "READY")
        2000 (do (timeout-fn 1000) "SET")
        (0 1000) (do (timeout-fn 1000) "GO!")
        (get-next-round-text game
                             millis-left
                             timeout-fn)))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Lifecycle Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- component-did-mount [arena game cmpnt-state]
  ;; Add resize listener
  (.addEventListener js/window
                     "resize"
                     (:resize-fn @cmpnt-state))
  (add-watch game :game-watcher
             (fn [key atom old-state new-state]
               (when (and (nil? old-state) (some? new-state))
                 (resize-canvas arena game)
                 (remove-watch game :game-watcher))))
  (resize-canvas arena game))

(defn- component-did-update [arena]

  (arena/arena @arena canvas-id))

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

(defn- game-play-title [game show-join-button]
  (let [{:keys [:game/id
                :game/is-private
                :game/frame
                :game/start-time
                :game/status]} @game
        round-start-time (:frame/round-start-time frame)
        round-number (:frame/round-number frame)]

    [:div.game-play-title-container

     [:h1.game-play-title
      (case status
        :closed
        "GAME OVER"

        (:pending-open
         :pending-closed
         :active-intermission)
        [:span (str "ROUND " round-number " STARTS IN: ")
         [countdown-timer (or round-start-time start-time)]]

        :active
        (str "ROUND " round-number)

        nil)]

     (when (and show-join-button (= status :pending-open))
       [join-button {:is-private is-private
                     :on-click (open-join-game-modal-fn id)}])]))

(defn- right-game-play-panel
  [game messages user]
  (let [{:keys [:game/name
                :game/end-time
                :game/players
                :game/max-players
                :game/num-rounds]} @game
        game-type (:game/type @game)
        in-game (get-player-by-username (:user/github-username @user)
                                        players)]

    ;; Dispatch winner modal if there's a winner
    (when end-time
      (show-winner-modal players))

    [:div.right-game-play-panel

     [:div.top-panel
      [game-play-title game (not in-game)]
      [:h2.game-play-subtitle
       (clojure.string/join " - "
                            [name
                             (get game-type-str-map game-type)
                             (str num-rounds " Rounds")])]
      [:p.wombat-counter
       (str "Wombats: " (count players) "/" max-players)]
      [ranking-box game]]

     (when in-game
       [:div.chat-panel
        [:div.chat-title [:span "CHAT"]]
        [chat-box game messages]])]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main Method
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn game-play [{:keys [game-id]}]
  (let [arena (re-frame/subscribe [:game/arena])
        game (re-frame/subscribe [:games/game-by-id game-id])
        cmpnt-state (reagent/atom {:resize-fn #(on-resize arena game)
                                   :update nil})
        messages (re-frame/subscribe [:game/messages])
        user (re-frame/subscribe [:current-user])]

    (reagent/create-class
     {:component-will-mount #(component-will-mount game-id)
      :component-did-mount #(component-did-mount arena game cmpnt-state)
      :component-did-update #(component-did-update arena)
      :component-will-unmount #(component-will-unmount game-id cmpnt-state)
      :display-name "game-play-panel"
      :reagent-render
      (fn [{:keys [game-id]}]
        (let [game-over (:game/end-time @game)
              transition-text (get-transition-text @game cmpnt-state)]
          (arena/arena @arena canvas-id)
          ;; Trigger rerender for transition screen
          (:update @cmpnt-state)
          [:div {:class-name root-class}
           [:div.left-game-play-panel {:class (when (or game-over
                                                        transition-text)
                                                "disabled")}
            [:span.transition-text
             transition-text]
            [:canvas {:id canvas-id}]]

           [right-game-play-panel game messages user]]))})))
