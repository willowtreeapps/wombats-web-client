(ns wombats-web-client.panels.games
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.events.games :refer [get-all-games]]
            [wombat-web-client.components.cards.game :refer [game-card]]))

;; Games Panel

(defonce empty-open-page "Sorry, there are no games to join at the moment.")
(defonce empty-joined-page "You haven’t joined any games yet. Start playing now!")

(defn- open-game-polling
  "Poll for newly created games every minute when viewing games panel and repopulate app state"
  []
 (js/setInterval #(get-all-games) 60000))


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

(defn my-game-toggle [cmpnt-state]
  (let [current-state (:show-my-games @cmpnt-state)]
    [:div.my-game-toggle-wrapper
     [:div.checkbox {:class (when current-state "selected")
                     :on-click #(swap! cmpnt-state assoc :show-my-games (not current-state))}] 
     [:div.desc "SHOW MY GAMES"]]))

(defn get-games [{:keys [show-open show-my-games open closed my-open my-closed]}]
  (cond
   (and show-open show-my-games) my-open
   (and (not show-open) show-my-games) my-closed
   (and show-open (not show-my-games)) open
   (and (not show-open) (not show-my-games)) closed))

(defn main-panel [cmpnt-state]
  (let [open-games (re-frame/subscribe [:open-games])
        closed-games (re-frame/subscribe [:closed-games])
        my-open (re-frame/subscribe [:my-open-games])
        my-closed (re-frame/subscribe [:my-closed-games])
        polling (open-game-polling)]

    (get-all-games)

    (fn []
      (swap! cmpnt-state assoc :polling polling)
      (let [open @open-games
            closed @closed-games
            show-my-games (:show-my-games @cmpnt-state)
            show-open (:show-open @cmpnt-state)
            games (get-games {:show-open show-open
                              :show-my-games show-my-games
                              :open open
                              :closed closed
                              :my-open @my-open
                              :my-closed @my-closed})]
        [:div.games-panel
         [:div.toggles
          [tab-view-toggle cmpnt-state]
          [my-game-toggle cmpnt-state]]
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
                                   :show-my-games false
                                   :polling nil})]
    (reagent/create-class
     {:component-will-unmount #(js/clearInterval (:polling @cmpnt-state))
      :reagent-render
      (fn []
        (let [current-user @current-user]
          (if-not current-user
            [login-prompt]
            [main-panel cmpnt-state])))})))
