(ns wombats-web-client.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [re-frisk.core :refer [enable-re-frisk!]]
              [wombats-web-client.events]
              [wombats-web-client.events.spritesheet :refer [get-spritesheet]]
              [wombats-web-client.subs]
              [wombats-web-client.routes :as routes]
              [wombats-web-client.views :as views]
              [wombats-web-client.config :as config]
              [wombats-web-client.socket-dispatcher :as sd]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (enable-re-frisk!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

;; TODO Find a good place for this and
;; also reconnect on disconnect
(sd/init-ws-connection)

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (re-frame/dispatch [:bootstrap-app])
  (get-spritesheet)
  (dev-setup)
  (mount-root))
