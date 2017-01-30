(ns wombats-web-client.panels.account
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.constants.urls :refer [github-signout-url]]
            [wombats-web-client.components.add-button :as add-wombat-button]
            [wombats-web-client.components.modals.add-wombat-modal :refer [add-wombat-modal]]))

;; User Account Panel

(defn open-add-wombat-modal []
  (fn []
    (re-frame/dispatch [:set-modal add-wombat-modal])))

(defn welcome []
  [:div (str "This is the Account Management Page.")
   [:div [:a {:href github-signout-url} "Log out"]]
   [add-wombat-button/root (open-add-wombat-modal)]])

(defn login-prompt []
  [:div (str "You must login to see your account.")])

(defn account []
  (let [current-user (re-frame/subscribe [:current-user])]
    (fn []
      (if (nil? @current-user)
        [login-prompt]
        [welcome]))))
