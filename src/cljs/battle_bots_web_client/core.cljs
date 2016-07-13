(ns battle_bots_web_client.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [devtools.core :as devtools]
              [battle_bots_web_client.handlers]
              [battle_bots_web_client.subs]
              [battle_bots_web_client.routes :as routes]
              [battle_bots_web_client.views :as views]
              [battle_bots_web_client.config :as config]))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")
    (devtools/install!)))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
