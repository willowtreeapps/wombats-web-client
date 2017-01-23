(ns wombats-web-client.panels.my-games
  (:require [re-frame.core :as re-frame]))

;; My Games Panel

(defn welcome []
  (fn []
    [:div (str "This is the My Games Page.")
     [:div [:a {:href "#/"} "go to Available Games page"]]]))

(defn my-games []
  (welcome))
