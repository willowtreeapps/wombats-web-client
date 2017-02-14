(ns wombats-web-client.components.navbar
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.constants.urls :refer [github-signin-url]]))

(defn login []
  [:a {:href github-signin-url} "Login"])

(defn wombat-logo []
  [:a {:href "#/"} [:img.wombat-logo {:src "/images/img-logo-horizontal.svg"}]])

(defn nav-link
  [{:keys [id class on-click link title current]}]
  [:li {:id id 
        :class class 
        :on-click on-click} 
   [:a {:class (if (= current id) "active") 
        :href link} title]])

(defn nav-links 
  [user state]
  (let [current-selected (:selected @state)]
    [:ul.navbar
     [nav-link {:id "games"
                :class "regular-link"
                :on-click #(swap! state assoc :selected "games")
                :link "#/"
                :title "GAMES"
                :current current-selected}]

     (if-not user
       [:li {:class "regular-link account"}
        [:a {:href github-signin-url} "LOGIN"]]
       [nav-link {:id "account"
                  :class "regular-link account"
                  :on-click #(swap! state assoc :selected "account")
                  :link "#/account"
                  :title "MY WOMBATS"
                  :current current-selected}])]))

(defn root
  []
  (let [current-user (re-frame/subscribe [:current-user])
        state (reagent/atom {:selected "games"})]
    (fn []
      [:div.navbar-component
       [wombat-logo]
       [nav-links @current-user state]])))
