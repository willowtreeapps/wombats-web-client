(ns wombats_web_client.views
    (:require [re-frame.core :as re-frame]
              [re-com.core :as re-com]

              ;; Panels
              [wombats_web_client.panels.home :as home]
              [wombats_web_client.panels.about :as about]

              ;; Components
              [wombats_web_client.components.navbar :as navbar]))

;; main

(defmulti panels identity)
(defmethod panels :home-panel [] [home/home-panel])
(defmethod panels :about-panel [] [about/about-panel])
(defmethod panels :default [] [:div])

(defn show-panel
  [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [:div
        [navbar/root]
        [:div.main-container
         [show-panel @active-panel]]])))
