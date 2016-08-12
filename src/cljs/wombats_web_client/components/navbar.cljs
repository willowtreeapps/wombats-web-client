(ns wombats_web_client.components.navbar
  (:require [re-frame.core :as re-frame]
            [wombats_web_client.utils.user :refer [isAdmin? isUser?]]))

(defn authenticated-links [user]
  [{:on-click #(println "TODO toggle menu")
    :display (:login user)
    :class-name "user-menu-button"
    :children [:ul.user-menu {:class-name "open"}
               [:li.user-menu-link
                 [:a {:href "#/settings"} "Settings"]]
               [:li.user-menu-link
                 [:a {:href "#/games"} "Games"]]
               [:li.user-menu-link
                 [:a {:href "#/signout"} "Sign out"]]]}])


(def unauthenticated-links [{:path "http://52.91.73.222/signin/github"
                             :display "Sign In"}])

(def common-links [{:path "#/"
                    :display "Wombats"
                    :class-name "home-link"}
                   {:path "#/about"
                    :display "About"}])

(defn resolve-navbar-items
  "renders role dependent links"
  [user]
  (cond
   (isUser? user) (concat common-links (authenticated-links user))
   :else (concat common-links unauthenticated-links)))

(defn render-item
  "Renders a single navbar item"
  [item]
  (fn []
    (let [isButton? (contains? item :on-click)]
      (if isButton?
        ;; We differentiate buttons from links by the presence of
        ;; an on-click event

        ;; Button Render
        [:li.navbar-button {:class-name (:class-name item)}
         [:button {:on-click (:on-click item)} (:display item)
          (:children item)]]

        ;; Link Render
        [:li.navbar-link {:class-name (:class-name item)}
         [:a {:href (:path item)} (:display item)]]))))

(defn root
  "Navbar container"
  []
  (let [user (re-frame/subscribe [:user])]
    (fn []
      [:nav.navbar
       [:ul.navbar-list
        (for [item (resolve-navbar-items @user)]
          ^{:key (:display item)} [render-item item])]])))
