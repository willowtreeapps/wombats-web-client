(ns wombats-web-client.panels.account
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.constants.urls :refer [github-signout-url]]
            [wombats-web-client.components.add-button :as add-wombat-button]
            [wombats-web-client.components.modals.add-wombat-modal :refer [add-wombat-modal]]
            [wombats-web-client.components.cards.wombat :as wombat-card]))

;; User Account Panel

(defn open-add-wombat-modal []
  (fn []
    (re-frame/dispatch [:set-modal add-wombat-modal])))

(defn temp-prettify-wombat [wombat]
  [:div {:key (:id wombat)}
   [:div "wombat image"]
   [:div (:name wombat)]])

(defn welcome []
  (let [my-wombats (re-frame/subscribe [:my-wombats])]
    [:div.account (str "This is the Account Management Page.")
     [:div "MY WOMBATS"]
     [:div.logout [:a {:href github-signout-url} "LOG OUT"]]
     [:div (map wombat-card/root @my-wombats)]
     [add-wombat-button/root (open-add-wombat-modal)]]))

(defn login-prompt []
  [:div (str "You must login to see your account.")])

(defn account []
  (let [current-user (re-frame/subscribe [:current-user])]
    (fn []
      (if (nil? @current-user)
        [login-prompt]
        [welcome]))))
