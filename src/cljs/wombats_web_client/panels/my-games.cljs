(ns wombats-web-client.panels.my-games
  (:require [re-frame.core :as re-frame]))

;; My Games Panel

(defn temp-my-game-card [game]
  [:li {:key (:game/id game)}
   [:div {:style {:color "white"}} (str game)]
   [:a {:href (str "#/my-games/" (:game/id game))} "View"]])

(defn welcome []
  (let [my-games (re-frame/subscribe [:my-games])]
    (fn []
      [:div.my-games-panel (str "This is the My Games Page.")
       [:ul.my-games-list (map temp-my-game-card @my-games)]])))

(defn login-prompt []
  (fn []
    [:div (str "You must login to see your games.")]))

(defn my-games []
  (let [current-user (re-frame/subscribe [:current-user])]
    (if (nil? @current-user) login-prompt welcome)))
