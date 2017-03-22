(ns wombats-web-client.routes
    (:require-macros [secretary.core :refer [defroute]])
    (:require [clojure.string :refer [split]]
              [secretary.core :as secretary]
              [pushy.core :as pushy]
              [re-frame.core :as re-frame]
              [wombats-web-client.utils.auth :refer [get-current-user-id
                                                     user-is-coordinator?]]))

(defonce history (pushy/pushy secretary/dispatch!
                              (fn [x]
                                ;; Split query params out
                                (when (secretary/locate-route (first (split x "?")))
                                  x))))
(defn app-routes []
  ;; define routes here

  (defroute "/" {:keys [query-params]}
    (js/console.log query-params)
    (re-frame/dispatch [:set-active-panel {:panel-id :view-games-panel
                                           :params query-params}]))

  (defroute "/games/:game-id" {:keys [game-id]}
    (re-frame/dispatch [:set-active-panel {:panel-id :game-play-panel
                                           :params {:game-id game-id}}]))

  (defroute "/config" []
    (if (user-is-coordinator?)
      (re-frame/dispatch
       [:set-active-panel
        {:panel-id :config-panel}])
      (re-frame/dispatch
       [:set-active-panel
        {:panel-id :page-not-found-panel}])))

  (defroute "/simulator" []
    (re-frame/dispatch [:set-active-panel {:panel-id :simulator-panel}]))

  (defroute "/welcome" {:keys [query-params]}
    (js/console.log query-params)
    (re-frame/dispatch [:set-active-panel {:panel-id :welcome-panel
                                           :params query-params}]))

  (defroute "/account" []
    (re-frame/dispatch [:set-active-panel {:panel-id :account-panel}]))

  (pushy/start! history))
