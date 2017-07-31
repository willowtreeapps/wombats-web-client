(ns wombats-web-client.components.navbar
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.constants.urls :refer [panel-router-map]]
            [wombats-web-client.routes :refer [nav!]]
            [wombats-web-client.utils.auth :refer [user-is-coordinator?]]))

(defn- nav-link-handler
  [evt url]
  (do
    (.preventDefault evt)
    (nav! url)))

(defn- wombat-logo []
  [:a {:href "/"
       :on-click #(nav-link-handler % "/")} [:img.wombat-logo {:src "/images/img-logo-horizontal.svg"}]])

(defn- nav-link
  [{:keys [id class on-click link title current]}]
  [:li {:id id
        :class class}
   [:a {:class (when (= current id) "active")
        :href link
        :on-click #(nav-link-handler % link)} title]])

(defn- coordinator-links [selected]
  [nav-link {:id "config"
             :class "regular-link"
             :link "/config"
             :title "CONFIG"
             :current selected}])

(defn- nav-links
  [selected]
  [:ul.navbar
   [nav-link {:id "games"
              :class "regular-link"
              :link "/"
              :title "GAMES"
              :current selected}]

   (when (user-is-coordinator?) [coordinator-links selected])

   [nav-link {:id "simulator"
              :class "regular-link"
              :link "/simulator"
              :title "SIMULATOR"
              :current selected}]

   [nav-link {:id "account"
              :class "regular-link account"
              :link "/account"
              :title "MY WOMBATS"
              :current selected}]])

(defn root
  []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      (let [active-panel @active-panel]
        (when active-panel
          (let [selected ((:panel-id active-panel) panel-router-map)]
            [:div.navbar-component
             [wombat-logo]
             [nav-links selected]]))))))
