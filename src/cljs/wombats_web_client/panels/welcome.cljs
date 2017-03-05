(ns wombats-web-client.panels.welcome
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.constants.urls :refer [github-signin-url]]))
;; Welcome Panel

(defonce welcome-page-title "Learn Clojure Through Gaming")
(defonce welcome-message-1 "Wombats is a platform for new developers to learn Clojure while playing through a fun and addictive game")
(defonce welcome-message-2 "Sign in with your GitHub account")

(defn render-wombat-logo []
  [:div.welcome-logo-container [:img {:src "/images/img-logo-horizontal.svg"}]])

(defn render-welcome-title []
  [:div.welcome-title-container welcome-page-title])

(defn render-welcome-message-content[msg]
  [:div.welcome-message-content {:key [msg]} msg])

(defn render-welcome-message []
  [:div.welcome-message-container (map render-welcome-message-content [welcome-message-1 welcome-message-2])])
 
(defn render-welcome-button[]
  [:div.welcome-button-container [:a {:href github-signin-url} "start playing now"]])

(defn welcome []
  [:div.welcome-panel
    [render-wombat-logo]
    [:div.welcome-content-container
      [render-welcome-title]
      [render-welcome-message]
      [render-welcome-button]]])