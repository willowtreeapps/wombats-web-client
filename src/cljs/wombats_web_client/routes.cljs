(ns wombats-web-client.routes
    (:require-macros [secretary.core :refer [defroute]])
    (:require [secretary.core :as secretary]
              [pushy.core :as pushy]
              [re-frame.core :as re-frame]
              [wombats-web-client.utils.local-storage :refer [get-token]]
              [wombats-web-client.utils.auth :refer [user-is-coordinator?]]))

(defonce history (pushy/pushy secretary/dispatch!
                              (fn [x]
                                (when (secretary/locate-route x) x))))

(defn logged-in-checker [logged-in-dispatch]
  (if (get-token) 
    (logged-in-dispatch)
    (pushy/set-token! history "/welcome")))

(defn app-routes []
  ;; define routes here

  (defroute "/" []
    (logged-in-checker #(re-frame/dispatch [:set-active-panel :view-games-panel])))

  (defroute "/games/:game-id" {game-id :game-id}
    (logged-in-checker (fn []
                         (re-frame/dispatch [:game/join-game game-id])
                         (re-frame/dispatch [:set-active-panel :game-play-panel]))))

  (defroute "/config" []
    (logged-in-checker (fn []
                         (if (user-is-coordinator?)
                           (re-frame/dispatch [:set-active-panel :config-panel])
                           (re-frame/dispatch [:set-active-panel :page-not-found-panel])))))

  (defroute "/simulator" []
    (logged-in-checker #(re-frame/dispatch [:set-active-panel :simulator-panel])))

  (defroute "/welcome" []
    (re-frame/dispatch [:set-active-panel :welcome-panel]))

  (defroute "/account" []
    (logged-in-checker #(re-frame/dispatch [:set-active-panel :account-panel])))

  (pushy/start! history))
