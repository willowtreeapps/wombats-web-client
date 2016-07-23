(ns wombats_web_client.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [devtools.core :as devtools]
              [wombats_web_client.routes :as routes]
              [wombats_web_client.views :as views]
              [wombats_web_client.config :as config]
              [wombats_web_client.handlers.root]
              [wombats_web_client.subs.root]))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")
    (devtools/install!)))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (dev-setup)
  (re-frame/dispatch-sync [:initialize-db])
  (re-frame/dispatch [:bootstrap-app])
  (routes/app-routes)
  (mount-root))
