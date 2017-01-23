(ns wombats-web-client.panels.account
  (:require [re-frame.core :as re-frame]))

;; User Account Panel

(defn welcome []
  (fn []
    [:div (str "This is the Account Management Page.")
     [:div [:a {:href "#/"} "go to Available Games page"]]]))

(defn account []
  (welcome))
