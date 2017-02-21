(ns wombat-web-client.components.cards.game
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.components.modals.join-wombat-modal :refer [join-wombat-modal]]))

(defn open-join-game-modal [game-id occupied-colors]
  (fn []
    (re-frame/dispatch [:set-modal #(join-wombat-modal game-id occupied-colors)])))

(defn get-arena-text-info [{:keys [type rounds width height]}]
  (str type " - " rounds " Rounds | " width "x" height))

(defn freq [freq-name amt]
  [:div.freq-object
   [:img {:src (str "/images/" freq-name ".png")}]
   [:div.freq-amt amt]])

(defn get-arena-frequencies [arena joined capacity]
  (let [{food :arena/food
         poison :arena/poison
         zakano :arena/zakano} arena
         ratio-joined (str joined "/" capacity)]
    [:div.arena-freq
     [freq "wombat_orange_right" ratio-joined]
     [freq "zakano_front" zakano]
     [freq "food_cherry" food]
     [freq "poison_vial2" poison]]))

(defn arena-card [is-joinable cmpnt-state game-id occupied-colors]
  (let [show-join-val (:show-join @cmpnt-state)]
    [:div.arena-preview
     [:img {:src "/images/mini-arena.png"}]
     (when is-joinable
       [:input.join-button {:class (when show-join-val "display")
                            :type "button"
                            :value "JOIN"
                            :onClick (open-join-game-modal game-id occupied-colors)}])]))

(defn get-occupied-colors [game]
  (let [players (:game/players game)]
    (reduce (fn [coll player] 
              (conj coll (:player/color player))) 
            [] players)))

(defn get-user-in-game [players current-user]
  (let [current-username (:user/github-username current-user)]
    (filter (fn [player]
              (let [user (:player/user player)
                    github-username (:user/github-username user)]
                (= github-username current-username))) players)))

(defn render-my-wombat-icon [player]
  (let [color (:player/color player)]
    [:div.wombat-preview-icon
     [:img {:src (str "images/wombat_" color "_right.png")}]]))


;; CARD STATES
;; is-joinable - OPEN & JOINABLE - :pending-open & not in-game
;; is-full - OPEN & FULL - :pending-closed 
;; is-playing - OPEN & ACTIVE - :active
;; is-finished - FINISHED - :closed 
;; States effect hoverstate and overlay design.
(defn game-card [game is-joinable]
  (let [cmpnt-state (reagent/atom {:show-join false})
        current-user (re-frame/subscribe [:current-user])
        {arena :game/arena
         game-id :game/id
         game-name :game/name
         game-players :game/players
         game-capacity :game/max-players
         game-rounds :game/num-rounds
         game-type :game/type
         game-status :game/status} game
        user-in-game (first (get-user-in-game game-players @current-user))
        game-joined-players (count game-players)
        {arena-width :arena/width
         arena-height :arena/height} arena
        occupied-colors (get-occupied-colors game)
        is-joinable (and (not= :closed game-status) (not= :pending-closed game-status) (nil? user-in-game))]
    (fn [game]
      [:a.game-card-link-wrapper {:href (str "#/games/" game-id)}
       [:div.game-card {:key game-id
                        :onMouseOver #(swap! cmpnt-state assoc :show-join true)
                        :onMouseOut #(swap! cmpnt-state assoc :show-join false)}
        [arena-card is-joinable cmpnt-state game-id occupied-colors]
        [:div.game-information
         (when (not-empty user-in-game) [render-my-wombat-icon user-in-game])
         [:div.text-info
          [:div.game-name game-name]
          [:div (get-arena-text-info {:type game-type
                                      :rounds game-rounds
                                      :width arena-width
                                      :height arena-height})]]
         [get-arena-frequencies arena game-joined-players game-capacity]]]])))
