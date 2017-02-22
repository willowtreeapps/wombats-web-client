(ns wombats-web-client.panels.games
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.events.games :refer [get-open-games
                                                     get-closed-games]]
            [wombat-web-client.components.cards.game :refer [game-card]]))

;; Games Panel

(defonce empty-open-page "Sorry, there are no games to join at the moment.")
(defonce empty-joined-page "You haven’t joined any games yet. Start playing now!")

(defn- open-game-polling
  "Poll for newly created games every minute when viewing games panel and repopulate app state"
  []
 (js/setInterval #(get-open-games) 60000))


(defn tab-view-toggle [cmpnt-state]
  (let [show-open (:show-open @cmpnt-state)]
    [:div.tab-game-toggle
     [:div.game-tab {:class (when show-open "active")
                     :onClick #(swap! cmpnt-state assoc :show-open true)} "OPEN"]
     [:div.game-tab {:class (when-not show-open "active")
                     :onClick #(swap! cmpnt-state assoc :show-open false)} "FINISHED"]]))

(defn empty-state [show-open]
  (let [empty-text (if show-open empty-open-page empty-joined-page)]
    [:div.empty-text empty-text]))

(defn main-panel [cmpnt-state]
  (let [open-games (re-frame/subscribe [:open-games])
        closed-games (re-frame/subscribe [:closed-games])
        polling (open-game-polling)]
    (get-open-games)
    (get-closed-games)
    (fn []
      (swap! cmpnt-state assoc :polling polling)
      (let [open @open-games
            closed @closed-games
            show-open (:show-open @cmpnt-state)
            games (if show-open open closed)]
        [:div.games-panel
         [tab-view-toggle cmpnt-state]
         [:div.games
          (if (pos? (count games))
            [:ul.games-list 
             (for [game games]
               ^{:key (:game/id game)} [game-card game])]
            [empty-state show-open])]]))))

(defn login-prompt []
  [:div "You must login to see open games."])

(defn games []
  (let [current-user (re-frame/subscribe [:current-user])
        cmpnt-state (reagent/atom {:show-open true
                                   :polling nil})]
    (reagent/create-class
     {:component-will-unmount #(js/clearInterval (:polling @cmpnt-state))
      :reagent-render
      (fn []
        (let [current-user @current-user]
          (if-not current-user
            [login-prompt]
            [main-panel cmpnt-state])))})))
