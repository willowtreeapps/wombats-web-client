(ns wombats-web-client.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [re-frisk.core :refer [enable-re-frisk!]]
            [wombats-web-client.events]
            [wombats-web-client.events.bootstrap :refer [bootstrap]]
            [wombats-web-client.subs]
            [wombats-web-client.routes :as routes]
            [wombats-web-client.views :as views]
            [wombats-web-client.config :as config]))

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (enable-re-frisk!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (dev-setup)
  (re-frame/dispatch-sync [:initialize-db])
  (routes/app-routes)
  (mount-root)
  (bootstrap))
