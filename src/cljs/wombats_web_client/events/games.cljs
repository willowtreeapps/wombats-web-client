(ns wombats-web-client.events.games
  (:require [re-frame.core :as re-frame]
            [ajax.core :refer [GET PUT]]
            [ajax.edn :refer [edn-request-format edn-response-format]]
            [wombats-web-client.constants.games :refer [pending-open
                                                        pending-closed
                                                        active
                                                        active-intermission
                                                        closed]]
            [wombats-web-client.utils.games
             :refer [build-status-query sort-players]]
            [wombats-web-client.constants.urls :refer [games-url
                                                       games-join-url]]
            [wombats-web-client.utils.auth :refer [add-auth-header
                                                   get-current-user-id]]))

(defn get-games [status on-success on-error]
  (GET games-url {:response-format (edn-response-format)
                  :keywords? true
                  :format (edn-request-format)
                  :headers (add-auth-header {})
                  :params {:status status}
                  :handler on-success
                  :error-handler on-error}))

(defn get-my-games [status on-success on-error]
  (GET games-url {:response-format (edn-response-format)
                  :keywords? true
                  :format (edn-request-format)
                  :headers (add-auth-header {})
                  :params {:status status
                           :user (get-current-user-id)}
                  :handler on-success
                  :error-handler on-error}))

(defn join-game [game-id wombat-id color password on-success on-error]
  (PUT (games-join-url game-id) {:response-format (edn-response-format)
                                 :keywords? true
                                 :format (edn-request-format)
                                 :headers (add-auth-header {})
                                 :handler on-success
                                 :error-handler on-error
                                 :params {:player/wombat-id wombat-id
                                          :player/color color
                                          :game/password password}}))

;; TODO Scaling Issue with Lots of games - only update with games that are new?

(defn get-open-games []
  (get-games
   (build-status-query [pending-open pending-closed active active-intermission])
   #(re-frame/dispatch [:games %])
   #(print "error on get open games")))

(defn get-my-open-games []
  (get-my-games
   (build-status-query [pending-open pending-closed active active-intermission])
   #(re-frame/dispatch [:games %])
   #(print "error on get my open games")))

(defn get-closed-games []
  (get-games
   (build-status-query [closed])
   #(re-frame/dispatch [:games %])
   #(print "error on get all closed games")))

(defn get-my-closed-games []
  (get-my-games
   (build-status-query [closed])
   #(re-frame/dispatch [:games %])
   #(print "error on get all closed games")))

(defn get-all-games []
  (get-open-games)
  (get-my-open-games)
  (get-closed-games)
  (get-my-closed-games))

(defn join-open-game [game-id wombat-id color password cb-success cb-error]
  (join-game
   game-id
   wombat-id
   color
   password
   cb-success
   cb-error))

(re-frame/reg-event-db
 :add-join-selection
 (fn [db [_ sel]]
   (update db :join-game-selections (fn [selections] (conj selections sel)))))

(re-frame/reg-event-db
 :games
 (fn [db [_ games]]
   (update-in
    db
    [:games]
    merge
    (reduce (fn [map game]
              (assoc map
                     (:game/id game)
                     (update game
                             :game/players
                             sort-players)))
            {}
            games))))

(re-frame/reg-fx
 :get-open-games
 (fn [_]
   (get-open-games)))

(re-frame/reg-fx
 :get-closed-games
 (fn [_]
   (get-closed-games)))
