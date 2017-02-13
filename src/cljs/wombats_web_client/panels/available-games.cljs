(ns wombats-web-client.panels.open-games
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.events.games :refer [get-open-games]]
            [wombat-web-client.components.cards.game :refer [game-card]]))

;; Open Games Panel

(defn temp-poll-button []
  [:input {:type "button"
           :value "GET GAMES"
           :onClick #(get-open-games)}])


(defn panel []
  (let [open-games (re-frame/subscribe [:open-games])]
    [:div.open-games
     [temp-poll-button]
     [:ul.open-games-list 
      (for [game @open-games]
        ^{:key (:game/id game)} [game-card game true])]]))

(defn login-prompt []
  [:div "You must login to see open games."])

(defn open-games []
  (let [current-user (re-frame/subscribe [:current-user])]
    (fn []
      (if-not @current-user
        [login-prompt]
        [panel]))))
