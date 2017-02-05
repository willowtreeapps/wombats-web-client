(ns wombats-web-client.events.in-game
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.utils.socket :as ws]))

(re-frame/reg-event-db
 :game/add-chat-message
 (fn [db [_ message]]
   (update db :game/messages (fn [messages]
                               (conj messages message)))))

(re-frame/reg-event-db
 :game/update-frame
 (fn [db [_ arena]]
   (assoc db :game/arena arena)))

(re-frame/reg-event-db
 :game/stats-update
 (fn [db [_ stats]]
   (assoc db :game/stats stats)))

(re-frame/reg-event-db
 :game/join-game
 (fn [db [_ game-id]]
   ;; TODO Add socket connection to bootstrap
   (js/setTimeout
    (fn []
      (ws/send-message :join-game {:game-id game-id})) 2000)
   db))
