(ns wombats-web-client.panels.open-games
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.events.games :refer [get-open-games
                                                     join-open-game]]
            [wombats-web-client.components.modals.join-wombat-modal :refer [join-wombat-modal]]))

;; Open Games Panel
;; #(join-open-game game-id (:id (first @wombats)) "white")

(defn temp-open-join-game-modal [game-id]
  (fn []
    (re-frame/dispatch [:set-modal #(join-wombat-modal game-id)])))

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
                            :onClick (temp-open-join-game-modal game-id)}]]))

(defn panel []
  (let [open-games (re-frame/subscribe [:open-games])]
    [:div.open-games-panel
     [:div (str "This is the Open Games page.")]
     [temp-poll-button]
     [:ul.open-games-list (map temp-game-card @open-games)]]))

(defn login-prompt []
  [:div (str "You must login to see open games.")])

(defn open-games []
  (let [current-user (re-frame/subscribe [:current-user])]
    (fn []
      (if (nil? @current-user)
        [login-prompt]
        [panel]))))
