(ns wombats-web-client.views
    (:require [re-frame.core :as re-frame]
              
              ;; Components
              [wombats-web-client.components.navbar :as navbar]

              ;; Panels
              [wombats-web-client.panels.available-games :as available-games-panel]
              [wombats-web-client.panels.my-games :as my-games-panel]
              [wombats-web-client.panels.account :as account-panel]))

;; main

(defn- panels [panel-name]
  (case panel-name
    :available-games-panel [available-games-panel/available-games]
    :my-games-panel [my-games-panel/my-games]
    :account-panel [account-panel/account]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])


(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [:div 
       [navbar/root]
       [show-panel @active-panel]])))
