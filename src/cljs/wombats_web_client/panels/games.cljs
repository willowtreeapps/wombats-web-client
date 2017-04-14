(ns wombats-web-client.panels.games
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.components.cards.game :refer [game-card]]
            [wombats-web-client.routes :refer [nav!]]
            [wombats-web-client.events.games :refer [get-open-games
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
                           (< parsed-page 1))
                     1 parsed-page)
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

(defn- page-link
  [query-params page-number]
  (construct-query-params (merge query-params
                                 {:page page-number})))

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
  ;; Subtract 1 since the API is 0-indexed
  (let [page (- page 1)]
    (cond
      (and closed mine)
      (get-my-closed-games page)

      (and closed (not mine))
      (get-closed-games page)

      (and (not closed) mine)
      (get-my-open-games page)

      (and (not closed) (not mine))
      (get-open-games page))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Lifecycle Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- component-will-receive-props
  [this [_ query-params]]
  (get-games query-params))

(defn- component-will-mount
  [query-params]
  (get-games query-params)

  ;; Make sure the url has valid query params
  (nav! (construct-query-params query-params)))

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

(defn get-empty-state [{:keys [closed mine]}]
  [:div.empty-text
   (cond
     (and closed mine)
     empty-my-finished-page

     (and (not closed) mine)
     empty-my-open-page

     (and closed (not mine))
     empty-finished-page

     (and (not closed) (not mine))
     empty-open-page)])

(defn get-user-in-game [players current-user]
  (let [current-username (:user/github-username current-user)]
    (filter (fn [player]
              (let [user (:player/user player)
                    github-username (:user/github-username user)]
                (= github-username current-username))) players)))

(defn- page-switcher [{:keys [page] :as query-params}]
  (let [prev-link (previous-page-link query-params)
        next-link (next-page-link query-params)
        page (js/Number page)]
    [:div.page-toggle
     [:a.nav-link
      {:class-name (when (= 1 page) "disabled")
       :href prev-link
       :on-click #(do
                    (.preventDefault %)
                    (nav! prev-link))} "PREVIOUS"]

     ;; This is where the possible pages go
     (map
      (fn [i]
        (let [new-page (+ i page)
              link (page-link query-params new-page)]
          [:a.page
           {:key (str "page-" i)
            :class-name (when (= i 0) "current")
            :href link
            :on-click #(do
                         (.preventDefault %)
                         (nav! link))}
           new-page]))
      (range 5))

     [:a.nav-link
      {:href next-link
       :on-click #(do
                    (.preventDefault %)
                    (nav! next-link))} "NEXT"]]))

(defn main-panel [{:keys [query-params]}]
  (let [current-user (re-frame/subscribe [:current-user])
        games (re-frame/subscribe [:games/games])]

    [:div.games-panel
     [:div.toggles
      [tab-view-toggle query-params]
      [my-game-toggle query-params]]

     [:div.games
      (if (empty? @games)
        [get-empty-state query-params]
        [:ul.games-list
         (doall
          (map
           (fn [[_ game]]
             (let [status (:game/status game)
                   players (:game/players game)
                   user-in-game (first
                                 (get-user-in-game
                                  players
                                  @current-user))
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
                                        num-joined]))
           @games))])

      [page-switcher query-params]]]))

(defn games [query-params]
  (reagent/create-class
   {:component-will-mount #(component-will-mount query-params)
    :component-will-receive-props component-will-receive-props
    :reagent-render (fn [query-params]
                      [main-panel {:query-params query-params}])}))
