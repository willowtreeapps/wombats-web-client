(ns wombats-web-client.components.navbar
  (:require [re-frame.core :as re-frame]))

(defn login []
  [:a {:href "http://54.145.152.66/api/v1/auth/github/signin"} "Login"])


(defn welcome [current-user]
  (print "welcome" current-user)
  [:div (str "hello " current-user ". This is the Available Games Page.")
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
