(ns wombats_web_client.handlers.ui
    (:require [re-frame.core :as re-frame]
              [wombats_web_client.db :as db]))

(defn- set-active-panel
  [db [_ active-panel meta]]
  (assoc db :active-panel {:panel active-panel :meta meta}))

(defn update-errors
  "adds an error to an error queue"
  [db [_ error]]
  (println error)
  db)

(re-frame/register-handler :set-active-panel set-active-panel)
(re-frame/register-handler :update-errors update-errors)
