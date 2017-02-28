(ns wombats-web-client.components.navbar
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [secretary.core :as secretary]
            [wombats-web-client.constants.urls :refer [github-signin-url
                                                       panel-router-map]]
            [wombats-web-client.utils.auth :refer [user-is-coordinator?]]))

(defn link-click-fn
  [link]
  (fn [evt]
    (.preventDefault evt)
    (secretary/dispatch! link)))

(defn login []
  [:a {:href github-signin-url} "Login"])

(defn wombat-logo []
  [:a {:href "/"} [:img.wombat-logo {:src "/images/img-logo-horizontal.svg"}]])

(defn nav-link
  [{:keys [id class on-click link title current]}]
  [:li {:id id
        :class class}
   [:a {:class (when (= current id) "active")
        :href link
        :onClick (link-click-fn link)} title]])

(defn coordinator-links [selected]
  [nav-link {:id "config"
             :class "regular-link"
             :link "#/config"
             :title "CONFIG"
             :current selected}])

(defn nav-links
  [user selected]
  [:ul.navbar
   [nav-link {:id "games"
              :class "regular-link"
              :link "/"
              :title "GAMES"
              :current selected}]
   
   (when (user-is-coordinator?) [coordinator-links selected])
   
   [nav-link {:id "simulator"
              :class "regular-link"
              :link "#/simulator"
              :title "SIMULATOR"
              :current selected}]

   (if-not user
     [:li {:class "regular-link account"}
      [:a {:href github-signin-url} "LOGIN"]]
     [nav-link {:id "account"
                :class "regular-link account"
                :link "/account"
                :title "MY WOMBATS"
                :current selected}])])

(defn root
  []
  (let [current-user (re-frame/subscribe [:current-user])
        active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      (let [active-panel @active-panel
            current-user @current-user]
        (when active-panel
          (let [selected ((keyword active-panel) panel-router-map)]
            [:div.navbar-component
             [wombat-logo]
             [nav-links current-user selected]]))))))
