(ns wombats_web_client.handlers.games
    (:require [re-frame.core :as re-frame]
              [wombats_web_client.db :as db]
              [wombats_web_client.utils.collection :refer [update-or-insert]]
              [wombats_web_client.services.wombats :refer [get-games
                                                           post-game
                                                           post-game-initialize
                                                           post-game-user]]))

(defn add-game
 "adds game to db"
 [db [_ game]]
 (assoc db :games (conj (:games db) game)))

(defn update-games
  "updates all games in state"
  [db [_ games]]
  (assoc db :games games))

(defn update-game
  "updates a single game in state"
  [db [_ game]]
  (assoc db :games (update-or-insert (:games db) game)))

(defn fetch-games
  "fetch all games"
  [db _]
  (get-games
    #(re-frame/dispatch [:update-games %])
    #(re-frame/dispatch [:update-errors %]))
    db)

(defn create-game
  "create game in db"
  [db _]
  (post-game
    #(re-frame/dispatch [:add-game %])
    #(re-frame/dispatch [:update-errors]))
  db)

(defn initialize-game
  "initializes game so players can join"
  [db [_ game-id]]
  (post-game-initialize game-id
    #(re-frame/dispatch [:update-game %])
    #(re-frame/dispatch [:update-errors %]))
  db)

(defn register-user-in-game
  "registers a user in a game"
  [db [_ game-id user-id repo]]
  (post-game-user game-id user-id repo
    #(re-frame/dispatch [:update-game %])
    #(re-frame/dispatch [:update-errors %]))
  db)

(re-frame/register-handler :add-game add-game)
(re-frame/register-handler :update-games update-games)
(re-frame/register-handler :update-game update-game)
(re-frame/register-handler :fetch-games fetch-games)
(re-frame/register-handler :create-game create-game)
(re-frame/register-handler :initialize-game initialize-game)
(re-frame/register-handler :register-user-in-game register-user-in-game)
