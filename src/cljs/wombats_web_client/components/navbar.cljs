(ns wombats-web-client.components.navbar
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.constants.urls :refer [github-signin-url]]))

(def selected (reagent/atom "open-games"))

(defn login []
  [:a {:href github-signin-url} "Login"])

(defn wombat-logo []
  [:a {:href "#/"} [:img.wombat-logo {:src "/images/img-logo-horizontal.svg"}]])

(defn nav-link
  [{:keys [id class on-click link title]}]
  [:li {:id id 
        :class class 
        :on-click on-click} 
   [:a {:class (if (= @selected id) "active") 
        :href link} title]])

(defn nav-links 
  [user]
  [:ul.navbar
   [nav-link {:id "open-games"
              :class "regular-link"
              :on-click #(reset! selected "open-games")
              :link "#/"
              :title "OPEN GAMES"}]
   [nav-link {:id "my-games"
              :class "regular-link"
              :on-click #(reset! selected "my-games")
              :link "#/my-games"
              :title "MY GAMES"}]
   (if (nil? user)
     [:li {:class "regular-link account"}
      [:a {:href github-signin-url} "LOGIN"]]
     [nav-link {:id "account"
                :class "regular-link account"
                :on-click #(reset! selected "account")
                :link "#/account"
                :title "MY WOMBATS"}])])

(defn root
  []
  (let [current-user (re-frame/subscribe [:current-user])]
    (fn []
      [:div.navbar-component
       [wombat-logo]
       [nav-links @current-user]])))
