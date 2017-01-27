(ns wombats-web-client.panels.account
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.constants.urls :refer [github-signout-url]]))

;; User Account Panel

(defn welcome []
  (fn []
    [:div (str "This is the Account Management Page.")
     [:div [:a {:href github-signout-url} "Log out"]]]))

(defn login-prompt []
  (fn []
    [:div (str "You must login to see your account.")]))

(defn account []
  (let [current-user (re-frame/subscribe [:current-user])]
    (if (nil? @current-user) login-prompt welcome)))
