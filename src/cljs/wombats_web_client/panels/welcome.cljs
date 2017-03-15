(ns wombats-web-client.panels.welcome
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.constants.urls :refer [github-signin-url]]
            [wombats-web-client.components.text-input
             :refer [text-input-with-label]]))
;; Welcome Panel

(defonce welcome-page-title
  "Learn Clojure Through Gaming")

(defonce welcome-message-1
  (str "Wombats is a platform for developers to learn new "
       "languages and hone their skills while playing through "
       "a fun and addictive game."))

(defonce welcome-message-2
  (str "Enter your access key and sign in with"
       "your GitHub account to start playing."))

(defn render-wombat-logo []
  [:div.welcome-logo-container
   [:img.logo
    {:src "/images/img-logo-horizontal.svg"}]])

(defn render-welcome-title []
  [:div.welcome-title-container welcome-page-title])

(defn render-welcome-message []
  [:div.welcome-message-container
   [:div.welcome-message-content welcome-message-1]
   [:div.welcome-message-content welcome-message-2]])

(defn render-welcome-button[]
  [:div.welcome-button-container
   [:a "get started now"]])

(defn render-access-form [cmpnt-state]
  [:div.access-form
   [text-input-with-label {:name "access-key"
                           :label "Access Key"
                           :state cmpnt-state
                           :is-password false}]
   [render-welcome-button]])

(defn render-login-prompt []
  [:div.login-prompt
   [:p "Already have an account?"]
   [:a.login {:href github-signin-url} "LOG IN"]])

(defn welcome []
  (let [cmpnt-state (reagent/atom {:access-key nil
                                   :access-key-error nil})]
    [:div.welcome-panel
     [render-wombat-logo]
     [:div.welcome-content-container
      [render-welcome-title]
      [render-welcome-message]
      [render-access-form cmpnt-state]
      [render-login-prompt]]
     [:div.copyright "© WillowTree, Inc. 2017"]]))
