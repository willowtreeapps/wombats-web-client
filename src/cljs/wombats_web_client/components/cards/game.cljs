(ns wombat-web-client.components.cards.game
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.components.modals.join-wombat-modal :refer [join-wombat-modal]]))

(defn open-join-game-modal [game-id occupied-colors]
  (fn []
    (re-frame/dispatch [:set-modal #(join-wombat-modal game-id occupied-colors)])))

(defn get-arena-text-info [{:keys [joined capacity rounds width height]}]
  (str joined "/" capacity " Players | " rounds " Rounds | " width "x" height))

(defn freq [freq-name amt]
  [:div.freq-object
   [:img {:src (str "/images/" freq-name ".png")}]
   [:div.freq-amt amt]])

(defn get-arena-frequencies [arena]
  (let [{food :arena/food
         poison :arena/poison
         zakano :arena/zakano} arena]
    [:div.arena-freq
     [freq "food_cherry" food]
     [freq "poison_vial2" poison]
     [freq "zakano_front" zakano]]))

(defn joinable-game-card [show-join game-id occupied-colors]
  (let [show-join-val @show-join]
    [:div.arena-preview
     [:img {:src "/images/mini-arena.png"}]
     [:input.join-button {:class (when show-join-val "display")
                          :type "button"
                          :value "JOIN"
                          :onClick (open-join-game-modal game-id occupied-colors)}]]))

(defn navigate-game-card [game-id]
  [:div.arena-preview
   [:a {:href (str "#/games/" game-id)} [:img {:src "/images/mini-arena.png"}]]])

(defn get-occupied-colors [game]
  (let [players (:game/players game)]
    (reduce (fn [coll player] 
              (conj coll (:player/color player))) 
            [] players)))

(defn game-card [game is-joinable]
  (let [show-join (reagent/atom false)
        {arena :game/arena
         game-id :game/id
         game-name :game/name
         game-players :game/players
         game-capacity :game/max-players
         game-rounds :game/num-rounds} game
        game-joined-players (count game-players)
        {arena-width :arena/width
         arena-height :arena/height} arena
        occupied-colors (get-occupied-colors game)]
    (fn [game is-joinable]
      [:div.game-card {:key game-id
                       :onMouseOver #(reset! show-join true)
                       :onMouseOut #(reset! show-join false)}
       (if is-joinable
         [joinable-game-card show-join game-id occupied-colors]
         [navigate-game-card game-id])
       [:div.game-information
        [:div.text-info
         [:div.game-name game-name]
         [:div (get-arena-text-info {:joined  game-joined-players
                                     :capacity game-capacity
                                     :rounds game-rounds
                                     :width arena-width
                                     :height arena-height})]]
        [get-arena-frequencies arena]]])))
