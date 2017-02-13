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
      ;; probably want to use doseq here
      ;; http://stackoverflow.com/questions/25327369/what-is-the-difference-among-the-functions-doall-dorun-doseq-and-for#answer-25330241
      (doall (for [game @open-games]
               ^{:key (:game/id game)} [game-card game true]))]]))

(defn login-prompt []
  ;; Dont think you need str here unless you want to interpolate
  [:div (str "You must login to see open games.")])

(defn open-games []
  (let [current-user (re-frame/subscribe [:current-user])]
    (fn []
      ;; if-not
      (if (nil? @current-user)
        [login-prompt]
        [panel]))))
