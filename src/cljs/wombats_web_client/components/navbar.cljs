(ns wombats-web-client.components.navbar
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.constants.urls :refer [panel-router-map]]
            [wombats-web-client.routes :refer [nav!]]
            [wombats-web-client.utils.auth :refer [user-is-coordinator?]]))

(def mobile-window-width 600)
(def wombat-logo-full "/images/img-logo-horizontal.svg")
(def wombat-logo-head "/images/img-logo-head.svg")


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Helper Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- get-mobile-status []
  (let [width (.-innerWidth js/window)]
    (< width mobile-window-width)))

(defn- on-resize [nav-status]
  (swap! nav-status assoc :mobile (get-mobile-status)))

(defn- toggle-nav-menu [nav-status]
  (swap! nav-status update-in [:visible] not))

(defn- hide-nav-menu [nav-status]
  (swap! nav-status assoc :visible false))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Render Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- wombat-logo [src]
  [:a {:href "/"} [:img.wombat-logo {:src src}]])

(defn- nav-link
  [{:keys [id class on-click link title current]}]
  [:li {:id id
        :class class}
   [:a {:class (when (= current id) "active")
        :href link
        :on-click #(do
                     (.preventDefault %)
                     (when on-click
                       (on-click))
                     (nav! link))} title]])

(defn- coordinator-links [selected nav-status]
  [nav-link {:id "config"
             :class "regular-link"
             :on-click #(hide-nav-menu nav-status)
             :link "/config"
             :title "CONFIG"
             :current selected}])

(defn- nav-links
  [selected nav-status]
  [:ul.navbar
   [nav-link {:id "games"
              :class "regular-link"
              :on-click #(hide-nav-menu nav-status)
              :link "/"
              :title "GAMES"
              :current selected}]

   (when (user-is-coordinator?) [coordinator-links selected nav-status])

   [nav-link {:id "simulator"
              :class "regular-link"
              :on-click #(hide-nav-menu nav-status)
              :link "/simulator"
              :title "SIMULATOR"
              :current selected}]

   [nav-link {:id "account"
              :class "regular-link account"
              :on-click #(hide-nav-menu nav-status)
              :link "/account"
              :title "MY WOMBATS"
              :current selected}]])

(defn- hamburger-menu [nav-status]
  [:button.nav-button
   {:on-click #(toggle-nav-menu nav-status)} "MENU"])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Lifecycle Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- component-did-mount [resize-fn]
  (.addEventListener js/window
                     "resize"
                     @resize-fn))

(defn- component-will-unmount [resize-fn]
  (.removeEventListener js/window
                        "resize"
                        @resize-fn))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main Method
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn root
  []
  (let [active-panel (re-frame/subscribe [:active-panel])
        nav-status (reagent/atom {:mobile (get-mobile-status)
                                  :visible false})
        resize-fn (reagent/atom #(on-resize nav-status))]
    (reagent/create-class
     {:component-did-mount #(component-did-mount resize-fn)
      :component-will-unmount #(component-will-unmount resize-fn)
      :reagent-render
      (fn []


        (when-let [active-panel @active-panel]
          (let [selected ((:panel-id active-panel) panel-router-map)]
            (if (:mobile @nav-status)
              [:div.mobile-navbar
               [:div.nav-items
                [wombat-logo wombat-logo-head]
                [hamburger-menu nav-status]]
               (when (:visible @nav-status)
                 [:div.nav-menu
                  [nav-links selected nav-status]])]
              [:div.navbar-component
               [wombat-logo wombat-logo-full]
               [nav-links selected nav-status]]))))})))
