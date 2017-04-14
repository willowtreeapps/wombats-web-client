(ns wombats-web-client.panels.games
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.events.games :refer [get-all-games]]
            [wombats-web-client.components.cards.game :refer [game-card]]
            [wombats-web-client.routes :refer [nav!]]
            [wombats-web-client.utils.games :refer [get-open-games
                                                    get-my-open-games
                                                    get-closed-games
                                                    get-my-closed-games]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Constants
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defonce empty-open-page
  "Sorry, there are no games to join at the moment.")
(defonce empty-my-open-page
  "You haven’t joined any games yet.")
(defonce empty-my-finished-page
  "None of your games have ended yet! Check back later.")
(defonce empty-finished-page
  "No games have ended yet! Check back later.")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Helpers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- construct-query-params
  [{:keys [closed page mine]}]
  ;; Make sure that if page is invalid, replace it with a 0
  ;; TODO: Use Cemerick
  (let [parsed-page (js/Number page)]
    (str "/?page=" (if (or (js/isNaN parsed-page)
                           (< parsed-page 0))
                     0 parsed-page)
         (when closed "&closed")
         (when mine "&mine"))))

(defn- nav-open!
  [query-params]
  (nav! (construct-query-params (dissoc query-params :closed))))

(defn- nav-finished!
  [query-params]
  (nav! (construct-query-params (merge query-params
                                       {:closed true}))))

(defn- toggle-mine!
  "This is triggered whenever you press SHOW MY GAMES"
  [{:keys [mine] :as query-params}]
  (nav! (construct-query-params (merge query-params
                                       {:mine (not mine)}))))

(defn- previous-page-link
  [{:keys [page] :as query-params}]
  (construct-query-params (merge query-params
                                 {:page (- (js/Number page) 1)})))

(defn- next-page-link
  [{:keys [page] :as query-params}]
  (construct-query-params (merge query-params
                                 {:page (+ (js/Number page) 1)})))

(defn- get-games
  "This is used to retrieve games based on params"
  [{:keys [closed mine page]}]
  #_(cond
    (and closed mine)
    (get-my-closed-games)

    (and closed (not mine))
    (get-closed-games)

    (and (not closed) mine)
    (get-my-open-games)

    (and (not closed) (not mine))
    (get-open-games)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Lifecycle Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- component-will-receive-props
  [this [_ query-params]]
  (js/console.log query-params))

(defn- component-will-mount
  "Poll for whichever games are in query-params"
  [query-params]
  (let [game-fn #(get-games query-params)]
    (game-fn)
    (js/setInterval game-fn
                    60000)

    ;; Make sure the url has valid query params
    (nav! (construct-query-params query-params))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Render Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn tab-view-toggle [{:keys [closed] :as query-params}]
  [:div.tab-game-toggle
   [:div.game-tab {:class (when-not closed "active")
                   :on-click #(nav-open! query-params)}
    "OPEN"]
   [:div.game-tab {:class (when closed "active")
                   :on-click #(nav-finished! query-params)}
    "FINISHED"]])

(defn my-game-toggle [{:keys [mine] :as query-params}]
  [:div.my-game-toggle-wrapper
   {:on-click #(toggle-mine! query-params)}
   [:div.checkbox {:class (when mine "selected")}]
   [:div.desc "SHOW MY GAMES"]])

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
        prev-link (previous-page-link query-params)
        next-link (next-page-link query-params)]

    [:div.games-panel
     [:div.toggles
      [tab-view-toggle query-params]
      [my-game-toggle query-params]]
     [:div.games

      ;; If there are games, then render them
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
                                      num-joined]))]

        [get-empty-state nil nil])

      ;; Render the page switcher
      [:div.page-toggle
       [:a {:class-name (when (= "0" (:page query-params)) "disabled")
            :href prev-link
            :on-click #(do
                         (.preventDefault %)
                         (nav! prev-link))} "PREVIOUS"]
       [:a {:href next-link
            :on-click #(do
                         (.preventDefault %)
                         (nav! next-link))} "NEXT"]]]]))

(defn games [query-params]
  (let [cmpnt-state (reagent/atom {:polling nil})]
    (reagent/create-class
     {:component-will-mount #(component-will-mount query-params)
      :component-will-receive-props component-will-receive-props
      :component-will-unmount #(js/clearInterval (:polling @cmpnt-state))
      :reagent-render (fn [query-params]
                        [main-panel {:cmpnt-state cmpnt-state
                                     :query-params query-params}])})))
