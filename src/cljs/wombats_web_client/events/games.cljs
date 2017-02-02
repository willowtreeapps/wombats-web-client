(ns wombats-web-client.events.games
  (:require [re-frame.core :as re-frame]
            [ajax.core :refer [GET PUT]]
            [ajax.edn :refer [edn-request-format edn-response-format]]
            [wombats-web-client.constants.urls :refer [games-url
                                                       games-join-url]]
            [wombats-web-client.utils.auth :refer [add-auth-header]]))

(defn get-pending-open-games [on-success on-error]
  (GET games-url {:response-format (edn-response-format)
                  :keywords? true
                  :format (edn-request-format)
                  :headers (add-auth-header {})
                  :params {:status "pending-open"}
                  :handler on-success
                  :error-handler on-error}))

(defn join-game [game-id wombat-id color on-success on-error]
  (PUT (games-join-url game-id) {:response-format (edn-response-format)
                          :keywords? true
                          :format (edn-request-format)
                          :headers (add-auth-header {})
                          :handler on-success
                          :error-handler on-error
                          :params {:player/wombat-id wombat-id :player/color color}}))

(defn get-my-games [on-success on-error]
  (GET games-url {:response-format (edn-response-format)
                  :keywords? true
                  :format (edn-request-format)
                  :headers (add-auth-header {})
                  :params {:status "pending-open"}
                  :handler on-success
                  :error-handler on-error}))

(defn get-open-games []
  (get-pending-open-games
   #(re-frame/dispatch [:open-games %])
   #(print "error on get open games")))

(defn get-all-my-games [] 
  (get-my-games
   #(re-frame/dispatch [:my-games %])
   #(print "error with getting my games")))

(defn join-open-game [game-id wombat-id color]
  (join-game
   game-id
   wombat-id
   color
   #(get-all-my-games)
   #(print "error with join-open-game")))

(re-frame/reg-event-db
 :open-games
 (fn [db [_ open-games]]
   (assoc db :open-games open-games)))

(re-frame/reg-event-db
 :my-games
 (fn [db [_ my-games]]
   (assoc db :my-games my-games)))
