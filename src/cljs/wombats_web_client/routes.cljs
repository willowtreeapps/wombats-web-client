(ns wombats-web-client.routes
    (:require-macros [secretary.core :refer [defroute]])
    (:require [secretary.core :as secretary]
              [pushy.core :as pushy]
              [re-frame.core :as re-frame]
              [wombats-web-client.utils.auth :refer [get-current-user-id
                                                     user-is-coordinator?]]))

(defonce history (pushy/pushy secretary/dispatch!
                              (fn [x]
                                (when (secretary/locate-route x) x))))
(defn app-routes []
  ;; define routes here

  (defroute "/" []
    (re-frame/dispatch [:set-active-panel {:panel-id :view-games-panel}]))

  (defroute "/games/:game-id" {game-id :game-id}
    (re-frame/dispatch [:set-active-panel {:panel-id :game-play-panel
                                           :params {:game-id game-id}}]))

  (defroute "/config" []
    (if (user-is-coordinator?)
      (re-frame/dispatch [:set-active-panel {:panel-id :config-panel}])
      (re-frame/dispatch [:set-active-panel {:panel-id :page-not-found-panel}])))

  (defroute "/simulator" []
    (re-frame/dispatch [:set-active-panel {:panel-id :simulator-panel}]))

  (defroute "/welcome" []
    (re-frame/dispatch [:set-active-panel {:panel-id :welcome-panel}]))

  (defroute "/account" []
    (re-frame/dispatch [:set-active-panel {:panel-id :account-panel}]))

  (pushy/start! history))
