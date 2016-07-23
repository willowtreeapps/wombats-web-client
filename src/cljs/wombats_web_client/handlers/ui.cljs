(ns wombats_web_client.handlers.ui
    (:require [re-frame.core :as re-frame]
              [wombats_web_client.db :as db]))

(defn- set-active-panel
  [db [_ active-panel]]
  (assoc db :active-panel active-panel))

(re-frame/register-handler :set-active-panel set-active-panel)
