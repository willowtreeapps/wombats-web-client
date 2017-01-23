(ns wombats-web-client.panels.available-games
  (:require [re-frame.core :as re-frame]))

;; Available Games Panel

(defn welcome []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [:div (str "hello from " @name ". This is the Available Games Page.")
       [:div [:a {:href "#/my-games"} "go to My Games page"]]
       [:div [:a {:href "#/account"} "go to My Wombats Account"]]])))

(defn available-games []
  (welcome))
