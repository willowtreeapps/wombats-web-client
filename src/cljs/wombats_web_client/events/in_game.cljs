(ns wombats-web-client.events.in-game
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.utils.games :refer [sort-players]]
            [wombats-web-client.utils.socket :as ws]))

(re-frame/reg-event-db
 :game/add-chat-message
 (fn [db [_ message]]
   (update db :game/messages (fn [messages]
                               (conj messages message)))))

(re-frame/reg-event-db
 :game/clear-chat-messages
 (fn [db [_ _]]
   (assoc db :game/messages [])))

(re-frame/reg-event-db
 :game/update-frame
 (fn [db [_ arena]]
   (assoc db :game/arena arena)))

(re-frame/reg-event-db
 :game/info
 (fn [db [_ info]]
   (let [game-id (:game/id info)]
     (update-in db
                [:games game-id]
                (fn [game]
                  (merge game
                         (update info
                                 :game/players
                                 sort-players)))))))
