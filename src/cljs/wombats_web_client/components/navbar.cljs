(ns wombats-web-client.components.navbar
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.constants.urls :refer [github-signin-url]]))

(defn login []
  [:a {:href github-signin-url} "Login"])


(defn welcome [current-user]
  (print "welcome" current-user)
  [:div (str "hello " current-user ".")
   [:div [:a {:href "#/my-games"} "go to My Games page"]]
   [:div [:a {:href "#/account"} "go to My Wombats Account"]]
   [:div [:a {:href "#/signout"} "Sign out of Wombats"]]])

(defn root
  []
  (let [current-user (re-frame/subscribe [:current-user])]
    (fn []
      (let [user @current-user]
        [:div
         (if (nil? user) (login) (welcome user))]))))
