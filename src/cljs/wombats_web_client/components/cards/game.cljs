(ns wombat-web-client.components.cards.game
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.utils.games :refer [get-user-in-game
                                                    get-game-state-str]]
            [wombats-web-client.components.modals.join-wombat-modal :refer [join-wombat-modal]]
            [wombats-web-client.components.navbar :refer [link-click-fn]]))

(defn open-join-game-modal-fn [game-id]
  (fn [e]
    (.preventDefault e)
    (re-frame/dispatch [:set-modal {:fn #(join-wombat-modal game-id)
                                    :show-overlay? true}])))

(defn get-arena-text-info [{:keys [type rounds width height]}]
  (let [round-txt (if (= 1 rounds) "Round" "Rounds")]
    (str type " - " rounds " " round-txt " | " width "x" height)))

(defn freq [freq-name amt]
  [:div.freq-object
   [:img {:class (when (= freq-name "food_cherry") "cherry")
          :src (str "/images/" freq-name ".png")}]
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

(defn arena-card [{:keys [is-private
                          is-joinable
                          is-full
                          is-playing
                          game-id]}]
  (let [game-state (get-game-state-str is-full is-playing)]

    [:div.arena-preview
     (when game-state
       [:div.game-state-wrapper
        [:div.state-overlay]
        [:div.game-state game-state]])
     [:img {:src "/images/mini-arena.png"}]
     (when is-joinable
       [:button {:class (str "join-button"
                             (when is-private " private"))
                 :onClick (open-join-game-modal-fn game-id)}
        "JOIN"])]))

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
(defn game-card [game user-in-game is-joinable is-full is-playing num-joined]
  (let [{arena :game/arena
         game-id :game/id
         game-name :game/name
         game-players :game/players
         game-capacity :game/max-players
         game-rounds :game/num-rounds
         game-type :game/type
         game-status :game/status
         game-private :game/is-private} game
        {arena-width :arena/width
         arena-height :arena/height} arena]

    (fn [game user-in-game is-joinable is-full is-playing num-joined]
      (let [href (str "/games/" game-id)]

        [:div.game-card {:key game-id}
         [:a.link {:href href
                   :onClick (link-click-fn href)}]

         [arena-card {:is-private game-private
                      :is-joinable is-joinable
                      :is-full is-full
                      :is-playing is-playing
                      :game-id game-id}]
         [:div.game-information
          (when (not-empty user-in-game) [render-my-wombat-icon user-in-game])
          [:div.text-info
           [:div.game-name game-name]
           [:div (get-arena-text-info {:type game-type
                                       :rounds game-rounds
                                       :width arena-width
                                       :height arena-height})]]
          [get-arena-frequencies arena num-joined game-capacity]]]))))
