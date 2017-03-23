(ns wombats-web-client.panels.welcome
  (:require [re-frame.core :as re-frame]
            [goog.string :refer [format]]
            [cljs.core.async :as async]
            [reagent.core :as reagent]
            [wombats-web-client.constants.urls :refer [github-signin-url]]
            [wombats-web-client.components.text-input
             :refer [text-input-with-label]])
  (:require-macros [cljs.core.async.macros :refer [go]]))


;; Welcome Panel

(defonce languages ["Clojure" "Python" "JavaScript"])

(defonce active-language
  (let [active-lang (reagent/atom (first languages))]
    (go
      (loop [languages languages]
        (let [lang (first languages)]
          (<! (async/timeout 1000))
          (reset! active-lang lang)
          (recur (conj (vec (rest languages)) lang)))))
    active-lang))

(defonce welcome-page-title
  "Learn %s Through Gaming")

;(format welcome-page-title "JavaScript")
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
  [:div.welcome-title-container
   (format welcome-page-title @active-language)])

(defn render-welcome-message []
  [:div.welcome-message-container
   [:div.welcome-message-content welcome-message-1]
   [:div.welcome-message-content welcome-message-2]])

(defn render-welcome-button [cmpnt-state]
  (let [url (str github-signin-url "?access-key=" (:access-key @cmpnt-state))]
    [:div.welcome-button-container
     [:a {:href url}
      "get started now"]]))

(defn render-access-form [cmpnt-state]
  [:div.access-form
   [text-input-with-label {:name "access-key"
                           :label "Access Key"
                           :state cmpnt-state
                           :is-password false}]
   [render-welcome-button cmpnt-state]])

(defn render-login-prompt []
  [:div.login-prompt
   [:p "Already have an account?"]
   [:a.login {:href github-signin-url} "LOG IN"]])

(defn welcome []
  (let [login-error (re-frame/subscribe [:login-error])
        cmpnt-state (reagent/atom {:access-key nil
                                   :access-key-error nil})]
    (reagent/create-class
     {:component-will-unmount #(re-frame/dispatch [:login-error nil])
      :reagent-render
      (fn []
        (when @login-error
          (swap! cmpnt-state assoc :access-key-error @login-error))

        [:div.welcome-panel
         [render-wombat-logo]
         [:div.welcome-content-container
          [render-welcome-title]
          [render-welcome-message]
          [render-access-form cmpnt-state]
          [render-login-prompt]]
         [:div.copyright "© WillowTree, Inc. 2017"]])})))
