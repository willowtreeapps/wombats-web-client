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

(defn display-modal
  "sets a new active modal"
  [db [_ modal]]
  (assoc db :active-modal modal))

(defn clear-modal
  "clears an active modal"
  [db _]
  (assoc db :active-modal nil))

(re-frame/register-handler :set-active-panel set-active-panel)
(re-frame/register-handler :update-errors update-errors)
(re-frame/register-handler :display-modal display-modal)
(re-frame/register-handler :clear-modal clear-modal)
