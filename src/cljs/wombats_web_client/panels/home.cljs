(ns wombats_web_client.panels.home
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]))

;; home

(defn home-title []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [re-com/title
       :label (str "Hello from " @name ". This is the Wombats Home Page.")
       :level :level1])))

(defn game-list
  [games]
  [:div
    [:ul.game-list
    (doall (for [game games]
            ^{:key (str (:_id game) "-" (count (:players game)))}
            [:li
              [:a {:href (str "#/preview/" (:_id game))} (:_id game)]]))]])

(defn home-panel []
  (re-frame/dispatch [:fetch-games])
  (let [games (re-frame/subscribe [:games])]
    (fn []
      [:div
        [home-title]
        [game-list @games]])))
