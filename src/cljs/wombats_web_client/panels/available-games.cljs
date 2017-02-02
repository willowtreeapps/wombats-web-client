(ns wombats-web-client.panels.open-games
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.events.games :refer [get-open-games
                                                     join-open-game]]))

;; Open Games Panel

(defn temp-poll-button []
  [:input {:type "button"
           :value "GET GAMES"
           :onClick #(get-open-games)}])

(defn temp-game-card [game]
  (let [game-id (:game/id game)
        wombats (re-frame/subscribe [:my-wombats])]
    [:li {:key game-id}
     [:div {:style {:color "white"}} (str game)]
     [:input.simple-button {:type "button"
                            :value "JOIN"
                            :onClick #(join-open-game game-id (:id (first @wombats)) "white")}]]))

(defn open-games []
  (let [open-games (re-frame/subscribe [:open-games])]
    (fn []
      [:div.open-games-panel
       [:div (str "This is the Open Games page.")]
       [temp-poll-button]
       [:ul.open-games-list (map temp-game-card @open-games)]])))
