(ns wombats_web_client.handlers.games
    (:require [re-frame.core :as re-frame]
              [wombats_web_client.db :as db]
              [wombats_web_client.utils.collection :refer [update-or-insert]]
              [wombats_web_client.services.wombats :refer [get-games]]))

(defn fetch-games
  "fetch all games"
  [db _]
  (get-games
    #(re-frame/dispatch [:update-games %])
    #(re-frame/dispatch [:update-errors %]))
    db)

(defn update-games
  "updates all games in state"
  [db [_ games]]
  (assoc db :games games))

(re-frame/register-handler :fetch-games fetch-games)
(re-frame/register-handler :update-games update-games)
