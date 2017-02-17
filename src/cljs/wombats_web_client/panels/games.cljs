(ns wombats-web-client.panels.games
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.events.games :refer [get-open-games]]
            [wombat-web-client.components.cards.game :refer [game-card]]))

;; Games Panel

(defonce empty-open-page "Sorry, there are no games to join at the moment.")
(defonce empty-joined-page "You haven’t joined any games yet. Start playing now!")

(defn temp-poll-button []
  [:input {:type "button"
           :value "GET GAMES"
           :onClick #(get-open-games)}])

(defn tab-view-toggle [cmpnt-state]
  (let [show-open (:show-open @cmpnt-state)]
    [:div.tab-game-toggle
     [:div.game-tab {:class (when show-open "active")
                     :onClick #(swap! cmpnt-state assoc :show-open true)} "OPEN"]
     [:div.game-tab {:class (when-not show-open "active")
                     :onClick #(swap! cmpnt-state assoc :show-open false)} "JOINED"]]))

(defn empty-state [show-open]
  (let [empty-text (if show-open empty-open-page empty-joined-page)]
    [:div.empty-text empty-text]))

(defn main-panel [cmpnt-state]
  (let [open-games (re-frame/subscribe [:open-games])
        joined-games (re-frame/subscribe [:joined-games])]
    (fn []
      (let [open @open-games
            joined @joined-games
            show-open (:show-open @cmpnt-state)
            games (if show-open open joined)]
        [:div.games-panel
         [tab-view-toggle cmpnt-state]
         [temp-poll-button]
         [:div.games
          (if games
            [:ul.games-list 
             (for [game games]
               ^{:key (:game/id game)} [game-card game true])]
            [empty-state show-open])]]))))

(defn login-prompt []
  [:div "You must login to see open games."])

(defn games []
  (let [current-user (re-frame/subscribe [:current-user])
        cmpnt-state (reagent/atom {:show-open true})]
    (fn []
      (let [current-user @current-user]
        (if-not current-user
          [login-prompt]
          [main-panel cmpnt-state])))))
