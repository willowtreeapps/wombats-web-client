(ns wombats_web_client.components.navbar
  (:require [re-frame.core :as re-frame]))


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
  (concat common-links unauthenticated-links))

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
    (print "navbar")
    (print @user)
    (print "test")
    (fn []
      [:nav.navbar
       [:ul.navbar-list
        (for [item (resolve-navbar-items @user)]
          ^{:key (:display item)} [render-item item])]])))
