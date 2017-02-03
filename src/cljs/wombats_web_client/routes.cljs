(ns wombats-web-client.routes
    (:require-macros [secretary.core :refer [defroute]])
    (:import goog.History)
    (:require [secretary.core :as secretary]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [re-frame.core :as re-frame]
              [wombats-web-client.events.user :refer [sign-out-event]]))

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  (secretary/set-config! :prefix "#")
  ;; --------------------
  ;; define routes here

  (defroute "/" []
    (re-frame/dispatch [:set-active-panel :open-games-panel]))

  (defroute "/my-games" []
    (re-frame/dispatch [:set-active-panel :my-games-panel]))

  (defroute "/my-games/:game-id" {game-id :game-id}
    (js/console.log (str "Game Id: " game-id))
    (re-frame/dispatch [:game/join-game game-id])
    (re-frame/dispatch [:set-active-panel :game-play-panel]))

  (defroute "/account" []
    (re-frame/dispatch [:set-active-panel :account-panel]))

  (defroute "/signout" []
    (sign-out-event)
    (set! (-> js/window .-location .-hash) "#/"))


  ;; --------------------
  (hook-browser-navigation!))
