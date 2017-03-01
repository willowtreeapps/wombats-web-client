(ns wombats-web-client.panels.games
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.events.games :refer [get-all-games]]
            [wombat-web-client.components.cards.game :refer [game-card]]))

;; Games Panel

(defonce empty-open-page "Sorry, there are no games to join at the moment.")
(defonce empty-my-open-page "You haven’t joined any games yet.")
(defonce empty-my-finished-page "None of your games have ended yet! Check back later.")
(defonce empty-finished-page "No games have ended yet! Check back later.")

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

(defn my-game-toggle [cmpnt-state]
  (let [current-state (:show-my-games @cmpnt-state)]
    [:div.my-game-toggle-wrapper {:on-click #(swap! cmpnt-state assoc :show-my-games (not current-state))}
     [:div.checkbox {:class (when current-state "selected")}] 
     [:div.desc "SHOW MY GAMES"]]))

(defn get-games [{:keys [show-open show-my-games open closed my-open my-closed]}]
  (cond
   (and show-open show-my-games) (sort-by :game/start-time my-open)
   (and (not show-open) show-my-games) (sort-by :game/end-time > my-closed)
   (and show-open (not show-my-games)) (sort-by :game/start-time open)
   (and (not show-open) (not show-my-games)) (sort-by :game/end-time > closed)))

(defn get-empty-state [show-open show-my-games]
  (cond
   (and show-open show-my-games) [:div.empty-text empty-my-open-page]
   (and show-open (not show-my-games)) [:div.empty-text empty-open-page]
   (and (not show-open) show-my-games) [:div.empty-text empty-my-finished-page]
   (and (not show-open) (not show-my-games)) [:div.empty-text empty-finished-page]))

(defn get-user-in-game [players current-user]
  (let [current-username (:user/github-username current-user)]
    (filter (fn [player]
              (let [user (:player/user player)
                    github-username (:user/github-username user)]
                (= github-username current-username))) players)))

(defn main-panel [cmpnt-state]
  (let [open-games (re-frame/subscribe [:open-games])
        closed-games (re-frame/subscribe [:closed-games])
        my-open (re-frame/subscribe [:my-open-games])
        my-closed (re-frame/subscribe [:my-closed-games])
        polling (open-game-polling)
        current-user (re-frame/subscribe [:current-user])]

    (get-all-games)

    (fn []
      (let  [current-user @current-user
             open @open-games
             closed @closed-games
             show-my-games (:show-my-games @cmpnt-state)
             show-open (:show-open @cmpnt-state)
             games (get-games {:show-open show-open
                               :show-my-games show-my-games
                               :open open
                               :closed closed
                               :my-open @my-open
                               :my-closed @my-closed})]


        (swap! cmpnt-state assoc :polling polling)

        [:div.games-panel
         [:div.toggles
          [tab-view-toggle cmpnt-state]
          [my-game-toggle cmpnt-state]]
         [:div.games
          (if (pos? (count games))
            [:ul.games-list 
             (for [game games]
               (let [status (:game/status game)
                     players (:game/players game)
                     user-in-game (first (get-user-in-game players current-user))
                     is-joinable (and (= :pending-open status) (nil? user-in-game))
                     is-full (= :pending-closed status)
                     is-playing (or (= :active status) (= :active-intermission status))
                     num-joined (count players)]

                 ^{:key (:game/id game)} [game-card game
                                          user-in-game
                                          is-joinable
                                          is-full
                                          is-playing
                                          num-joined]))]

            [get-empty-state show-open show-my-games])]]))))

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
