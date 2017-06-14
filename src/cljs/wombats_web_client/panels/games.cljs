(ns wombats-web-client.panels.games
  (:require [cemerick.url :refer [map->query url]]
            [re-frame.core :as re-frame]
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
  ;; Make sure that if page is invalid, replace it with a 1
  (let [page (js/Number page)
        page (if (or (js/isNaN page)
                     (< page 1))
               1 page)]

    (str "/?page=" page
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

(defn get-user-in-game [players current-user]
  (let [current-username (:user/github-username current-user)]
    (filter (fn [player]
              (let [user (:player/user player)
                    github-username (:user/github-username user)]
                (= github-username current-username))) players)))

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

(defn- page-selector-item
  [offset page query-params key]
  (let [new-page (+ page offset)
        link (page-link query-params new-page)]

    [:a.page
     {:key key
      :class-name (when (= new-page page) "current")
      :href link
      :on-click #(do
                   (.preventDefault %)
                   (nav! link))}
     new-page]))

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
     ;;[:span.page.ellipsis "..."]
     (let [new-page 1
           link (page-link query-params new-page)]
       [:a.page
        {:class-name (when (= new-page page) "current")
         :href link
         :on-click #(do
                      (.preventDefault %)
                      (nav! link))}
        new-page])

     ;; Generate dynamic page numbers for the middle items
     (let [total-items 4] ;; must be even -
       (map
        (fn [i]
          (let [key (str "page-" i)]
            (cond
              ;; First case: 2 3 4 5 ...
              (<= page 4) (if (= i total-items) ;; generate elipsis for last item
                         [:span.page.ellipsis {:key key} "..."]
                         (let [new-page (+ i 2)
                               link (page-link query-params new-page)]

                           [:a.page
                            {:key key
                             :class-name (when (= new-page page) "current")
                             :href link
                             :on-click #(do
                                          (.preventDefault %)
                                          (nav! link))}
                            new-page]))

              ;; Second case ... 4 5 6 ...
              (> page 4) (cond
                           (or (= i 0) (= i total-items)) [:span.page.ellipsis {:key key} "..."]
                           (= i (/ total-items 2))  (page-selector-item 0 page query-params key)
                           (<= i (- (/ total-items 2) 1)) (page-selector-item -1 page query-params key)
                           (>= i (+ (/ total-items 2) 1))  (page-selector-item 1 page query-params key)

                           ))))
        (range 0 (+ total-items 1)))) ;; page is 3, total 2  ->  1 ... 6 (1,2,3,4,5)

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
