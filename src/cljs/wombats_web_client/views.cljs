(ns wombats_web_client.views
    (:require [re-frame.core :as re-frame]
              [re-com.core :as re-com]

              ;; Panels
              [wombats_web_client.panels.home :as home]
              [wombats_web_client.panels.about :as about]
              [wombats_web_client.panels.preview-game :as preview-game]

              ;; Components
              [wombats_web_client.components.navbar :as navbar]))

;; main

(defmulti panels (fn [active-panel] (:panel active-panel)))
(defmethod panels :home-panel [_] [home/home-panel])
(defmethod panels :about-panel [_] [about/about-panel])
(defmethod panels :preview-game [{:keys [meta]}] [preview-game/preview-game-panel meta])
(defmethod panels :default [_] [:div])

(defn show-panel
  [panel]
  [panels panel])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [:div
        [navbar/root]
        [:div.main-container
         [show-panel @active-panel]]])))
