(ns wombats-web-client.routes
    (:require-macros [secretary.core :refer [defroute]])
    (:import goog.History)
    (:require [secretary.core :as secretary]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [re-frame.core :as re-frame]
              [wombats-web-client.events :refer [sign-out]]))

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
    (re-frame/dispatch [:set-active-panel :available-games-panel]))

  (defroute "/my-games" []
    (re-frame/dispatch [:set-active-panel :my-games-panel]))

  (defroute "/account" []
    (re-frame/dispatch [:set-active-panel :account-panel]))

  (defroute "/signout" []
    (sign-out)
    ;;(re-frame/dispatch [:set-active-panel :available-games-panel])
    (set! (-> js/window .-location .-hash) "#/"))


  ;; --------------------
  (hook-browser-navigation!))
