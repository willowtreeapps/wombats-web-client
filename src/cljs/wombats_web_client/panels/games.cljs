(ns wombats-web-client.panels.games
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.events.games :refer [get-all-games]]
            [wombats-web-client.components.cards.game :refer [game-card]]
            [wombats-web-client.utils.games :refer [get-open-games
                                                    get-my-open-games
                                                    get-closed-games
                                                    get-my-closed-games]]))

;; Constants

(defonce empty-open-page
  "Sorry, there are no games to join at the moment.")
(defonce empty-my-open-page
  "You haven’t joined any games yet.")
(defonce empty-my-finished-page
  "None of your games have ended yet! Check back later.")
(defonce empty-finished-page
  "No games have ended yet! Check back later.")

;; Helpers

(defn- parse-query-params
  [{:keys [closed page mine]}]
  (let [page-parsed (js/parseInt page)]
    {:page (if (js/isNaN page-parsed) 0 page-parsed)
     :mine (some? mine)
     :closed (some? closed)}))

;; Lifecycle Methods

(defn- component-will-mount
  "Poll for whichever games are in query-params"
  [query-params]
  (let [params (parse-query-params query-params)]
    (js/setInterval get-all-games 60000)))

;; Render Methods

(defn tab-view-toggle [show-closed]
  [:div.tab-game-toggle
   [:div.game-tab {:class (when-not show-closed "active")
                   :on-click #(prn "SHOW OPEN")}
    "OPEN"]
   [:div.game-tab {:class (when show-closed "active")
                   :on-click #(prn "SHOW CLOSED")}
    "FINISHED"]])

(defn my-game-toggle [show-my-games]
  [:div.my-game-toggle-wrapper
   {:on-click #(prn "TOGGLE MY GAMES")}
   [:div.checkbox {:class (when show-my-games "selected")}]
   [:div.desc "SHOW MY GAMES"]])

;; Helper Methods

(defn get-sorted-games [{:keys [show-open show-my-games games current-user]}]
  (cond
   (and show-open show-my-games)
   (sort-by :game/start-time (get-my-open-games games current-user))

   (and (not show-open) show-my-games)
   (sort-by :game/end-time > (get-my-closed-games games current-user))

   (and show-open (not show-my-games))
   (sort-by :game/start-time (get-open-games games))

   (and (not show-open)
        (not show-my-games))
   (sort-by :game/end-time > (get-closed-games games))))

(defn get-empty-state [show-open show-my-games]
  (cond
   (and show-open show-my-games)
   [:div.empty-text empty-my-open-page]

   (and show-open (not show-my-games))
   [:div.empty-text empty-open-page]

   (and (not show-open) show-my-games)
   [:div.empty-text empty-my-finished-page]

   (and (not show-open) (not show-my-games))
   [:div.empty-text empty-finished-page]))

(defn get-user-in-game [players current-user]
  (let [current-username (:user/github-username current-user)]
    (filter (fn [player]
              (let [user (:player/user player)
                    github-username (:user/github-username user)]
                (= github-username current-username))) players)))



(defn main-panel [{:keys [cmpnt-state query-params]}]
  (let [current-user (re-frame/subscribe [:current-user])
        games (re-frame/subscribe [:games/games])
        {page :page
         show-my-games :mine
         show-closed :closed} (parse-query-params query-params)]

    (js/console.log @games)

    ;; Based on the query parameters, start polling for games
    [:div.games-panel
     [:div.toggles
      [tab-view-toggle show-closed]
      [my-game-toggle show-my-games]]
     [:div.games
      (if true
        [:ul.games-list
         (for [game []]
           (let [status (:game/status game)
                 players (:game/players game)
                 user-in-game (first
                               (get-user-in-game
                                players
                                current-user))
                 is-joinable (and (= :pending-open status)
                                  (nil? user-in-game))
                 is-full (= :pending-closed status)
                 is-playing (or (= :active status)
                                (= :active-intermission status))
                 num-joined (count players)]

             ^{:key (:game/id game)} [game-card game
                                      user-in-game
                                      is-joinable
                                      is-full
                                      is-playing
                                      num-joined]))])

      [get-empty-state nil nil]]]))

(defn games [query-params]
  (let [cmpnt-state (reagent/atom {:polling nil})]
    (reagent/create-class
     {:component-will-mount #(component-will-mount query-params)
      :component-will-unmount #(js/clearInterval (:polling @cmpnt-state))
      :reagent-render
      (fn [query-params]
        [main-panel {:cmpnt-state cmpnt-state
                     :query-params query-params}])})))
