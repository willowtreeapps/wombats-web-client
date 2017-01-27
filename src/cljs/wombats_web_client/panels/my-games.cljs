(ns wombats-web-client.panels.my-games
  (:require [re-frame.core :as re-frame]))

;; My Games Panel

(defn welcome []
  (fn []
    [:div (str "This is the My Games Page.")]))

(defn login-prompt []
  (fn []
    [:div (str "You must login to see your games.")]))

(defn my-games []
  (let [current-user (re-frame/subscribe [:current-user])]
    (if (nil? @current-user) login-prompt welcome)))
