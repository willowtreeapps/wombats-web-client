(ns wombats-web-client.panels.games
  (:require [cemerick.url :refer [map->query url]]
            [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.components.cards.game :refer [game-card]]
            [wombats-web-client.routes :refer [nav!]]
            [wombats-web-client.events.games :refer [get-games-query-params
                                                     get-open-games
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
(defonce total-items
  4) ;; Number of items shown to the left and right of the page chooser
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

(defn- construct-nav-token
  []
  (let [{:keys [closed page mine]} @(re-frame/subscribe [:games/query-params])
        page (js/Number page)]
    (str "/?page=" page
         (when closed "&closed")
         (when mine "&mine"))))

(defn- nav-open!
  [query-params]
  (let [updated-params (dissoc @query-params :closed)]
    (nav! (construct-query-params updated-params))))

(defn- nav-finished!
  [query-params]
  (let [updated-params (merge @query-params {:closed true})]
    (nav! (construct-query-params updated-params))))

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
  (construct-query-params {:page (+ (js/Number page) 1)}))

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
  (println "received props")
  (get-games-query-params @query-params))

(defn- component-will-mount
  [query-params]
  (println "will mount")
  (get-games-query-params @query-params)

  ;; Make sure the url has valid query params
  (nav! (construct-query-params @query-params)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Render Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn tab-view-toggle [query-params]
  (let [closed (:closed @query-params)]
    [:div.tab-game-toggle
     [:div.game-tab {:class (when-not closed "active")
                     :on-click #(nav-open! query-params)}
      "OPEN"]
     [:div.game-tab {:class (when closed "active")
                     :on-click #(nav-finished! query-params)}
      "FINISHED"]]))

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

(defn- render-page-selector
  [{:keys [new-page page query-params key]}]
  (let [link (page-link query-params new-page)]
    [:a.page
     {:key key
      :class-name (when (= new-page page) "current")
      :href link
      :on-click #(do
                   (.preventDefault %)
                   (nav! link))}
     new-page]))

(defn- page-selector-item-offset
  [{:keys [offset page query-params key]}]
  (let [new-page (+ page offset)]
    (render-page-selector {:new-page new-page
                           :page page
                           :query-params query-params
                           :key key})))

(defn- calculate-offset
  "Given total-items and current index calculate the necessary offset"
  [total-items i]
  (get (vec (range (- (/ total-items 2)) (inc (/ total-items 2))))i))

(defn- render-ellipsis
  [key]
  [:span.page.ellipsis {:key key} "..."])

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

     (render-page-selector {:new-page 1
                            :page page
                            :query-params query-params
                            :key "page-1"})
     (map
      (fn [i]
        (let [key (str "page-" i)]
          (cond
            ;; First case: 2 3 4 5 ...
            (<= page 4) (if (= i total-items)
                          (render-ellipsis key)
                          (let [new-page (+ i 2)]
                            (render-page-selector {:new-page new-page
                                                   :page page
                                                   :query-params query-params
                                                   :key key})))

            ;; Second case ... 4 5 6 ...
            (> page 4) (cond
                         (or (zero? i) (= i total-items))
                         (render-ellipsis key)
                         (= i (/ total-items 2))
                         (page-selector-item-offset
                          {:offset 0
                           :page page
                           :query-params query-params
                           :key key})
                         (<= i (dec (/ total-items 2)))
                         (page-selector-item-offset
                          {:offset (calculate-offset total-items i)
                           :page page
                           :query-params query-params
                           :key key})
                         (>= i (inc (/ total-items 2)))
                         (page-selector-item-offset
                          {:offset (calculate-offset total-items i)
                           :page page
                           :query-params query-params
                           :key key})))))
      (range 0 (inc total-items)))

     ;; TODO max length goes here
     [:a.nav-link
      {:href next-link
       :on-click #(do
                    (.preventDefault %)
                    (nav! next-link))} "NEXT"]]))

(defn main-panel [query-params]
  (let [current-user (re-frame/subscribe [:current-user])
        games (re-frame/subscribe [:games/games])]
    (println @query-params)
    [:div.games-panel
     [:div.toggles
      [tab-view-toggle query-params]
      [my-game-toggle @query-params]]

     [:div.games
      (if (empty? @games)
        [get-empty-state @query-params]
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

      [page-switcher @query-params]]]))

(defn games []
  (let [query-params (re-frame/subscribe [:query-params])]
    (reagent/create-class
     {:component-will-mount #(component-will-mount query-params)
      :component-will-receive-props #(component-will-receive-props query-params)
      :reagent-render (fn []
                        [main-panel query-params])})))
