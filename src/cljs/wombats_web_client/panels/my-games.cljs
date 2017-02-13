(ns wombats-web-client.panels.my-games
  (:require [re-frame.core :as re-frame]
            [wombat-web-client.components.cards.game :refer [game-card]]))

;; My Games Panel

(defn welcome []
  (let [my-games (re-frame/subscribe [:my-games])]
    [:div.my-games
     [:ul.my-games-list
      ;; doseq
      (doall (for [game @my-games]
               ^{:key (:game/id game)} [game-card game false]))]]))

(defn login-prompt []
  (fn []
    [:div (str "You must login to see your games.")]))

(defn my-games []
  (let [current-user (re-frame/subscribe [:current-user])]
    (fn []
      ;; if-not
      (if (nil? @current-user)
        [login-prompt]
        [welcome]))))
