(ns wombat-web-client.components.cards.game
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.components.modals.join-wombat-modal :refer [join-wombat-modal]]))

(defn open-join-game-modal [game-id]
  (fn []
    (re-frame/dispatch [:set-modal #(join-wombat-modal game-id)])))

(defn get-arena-text-info [{:keys [joined capacity rounds width height]}]
  (str joined "/" capacity " Players | " rounds " Rounds | " width "x" height))

(defn freq [freq-name type]
  [:div.freq-object
   [:img {:src (str "/images/" freq-name ".png")}]
   [:div.freq-amt "Medium"]])

(defn get-arena-frequencies []
  [:div.arena-freq
   [freq "food_cherry"]
   [freq "poison_vial2"]
   [freq "steelwall_1"]
   [freq "zakano_front"]
   [freq "woodwall_1"]])

(defn joinable-game-card [show-join game-id]
  (let [show-join-val @show-join]
    [:div.arena-preview
     [:img {:src "/images/mini-arena.png"}]
     [:input.join-button {:class (when show-join-val "display")
                          :type "button"
                          :value "JOIN"
                          :onClick (open-join-game-modal game-id)}]]))

(defn navigate-game-card [game-id]
  [:div.arena-preview
   [:a {:href (str "#/games/" game-id)} [:img {:src "/images/mini-arena.png"}]]])

(defn game-card [game is-joinable]
  (let [show-join (reagent/atom false)
        game-id (:game/id game)
        game-name (:game/name game)
        game-joined-players (count (:game/players game))
        game-capacity (:game/max-players game)
        game-rounds (:game/num-rounds game)
        arena-width (:arena/width (:game/arena game))
        arena-height (:arena/height (:game/arena game))
        wombats (re-frame/subscribe [:my-wombats])]
    (fn []
      [:div.game-card {:key game-id
                       :onMouseOver #(reset! show-join true)
                       :onMouseOut #(reset! show-join false)}
       (if is-joinable
         [joinable-game-card show-join game-id]
         [navigate-game-card game-id])
       [:div.game-information
        [:div.text-info
         [:div.game-name game-name]
         [:div (get-arena-text-info {:joined  game-joined-players
                                     :capacity game-capacity
                                     :rounds game-rounds
                                     :width arena-width
                                     :height arena-height})]]
        [get-arena-frequencies]]])))
