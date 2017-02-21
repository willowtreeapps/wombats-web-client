(ns wombats-web-client.events.games
  (:require [re-frame.core :as re-frame]
            [ajax.core :refer [GET PUT]]
            [ajax.edn :refer [edn-request-format edn-response-format]]
            [wombats-web-client.constants.urls :refer [games-url
                                                       games-join-url]]
            [wombats-web-client.utils.auth :refer [add-auth-header get-current-user-id]]))

(defn get-games [status on-success on-error]
  (GET games-url {:response-format (edn-response-format)
                  :keywords? true
                  :format (edn-request-format)
                  :headers (add-auth-header {})
                  :params {:status status}
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

(defn get-joined-games [id on-success on-error]
  (GET games-url {:response-format (edn-response-format)
                  :keywords? true
                  :format (edn-request-format)
                  :headers (add-auth-header {})
                  :params {:user id}
                  :handler on-success
                  :error-handler on-error}))

;; TODO Scaling Issue with Lots of games - only update with games that are new?
(defn get-pending-open-games []
  (get-games
   "pending-open"
   #(re-frame/dispatch [:pending-open-games %])
   #(print "error on get open games")))

(defn get-closed-games []
  (get-games
   "closed"
   #(re-frame/dispatch [:closed-games %])
   #(print "error on get all closed games")))

(defn get-all-joined-games [user-id]
  (get-joined-games
   user-id
   #(re-frame/dispatch [:joined-games %])
   #(print "error with getting my games")))

(defn join-open-game [game-id wombat-id color password cb-success]
  (join-game
   game-id
   wombat-id
   color
   password
   (fn []
     (cb-success)
     (get-all-joined-games (get-current-user-id)))
   (fn [error]
     (re-frame/dispatch [:update-modal-error (:message (:response error))]))))

(re-frame/reg-event-db
 :pending-open-games
 (fn [db [_ pending-open-games]]
   (assoc db :pending-open-games pending-open-games)))

(re-frame/reg-event-db
 :closed-games
 (fn [db [_ closed-games]]
   (assoc db :closed-games closed-games)))

(re-frame/reg-event-db
 :joined-games
 (fn [db [_ joined-games]]
   (assoc db :joined-games joined-games)))

(re-frame/reg-event-db
 :add-join-selection
 (fn [db [_ sel]]
   (update db :join-game-selections (fn [selections] (conj selections sel)))))

(re-frame/reg-fx
 :get-pending-open-games
 (fn [_]
   (get-pending-open-games)))

(re-frame/reg-fx
 :get-joined-games
 (fn [id]
   (get-all-joined-games id)))

(re-frame/reg-fx
 :get-closed-games
 (fn [_]
   (get-closed-games)))
