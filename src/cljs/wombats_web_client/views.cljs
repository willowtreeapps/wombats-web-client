(ns wombats-web-client.views
    (:require [re-frame.core :as re-frame]
              
              ;; Components
              [wombats-web-client.components.navbar :as navbar]

              ;; Panels
              [wombats-web-client.panels.open-games :as open-games-panel]
              [wombats-web-client.panels.my-games :as my-games-panel]
              [wombats-web-client.panels.account :as account-panel]
              [wombats-web-client.panels.game-play :as game-play-panel]))

;; mainutil

(defn- panels [panel-name]
  (case panel-name
    :open-games-panel [open-games-panel/open-games]
    :my-games-panel [my-games-panel/my-games]
    :account-panel [account-panel/account]
    :game-play-panel [game-play-panel/game-play]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn display-overlay
  [modal-form]
  (if (not (nil? modal-form)) [:div.overlay]))

(defn display-modal
  [modal-form]
  (if (not (nil? modal-form)) [modal-form]))

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])
        modal (re-frame/subscribe [:modal])]
    (fn []
      (let [current-modal @modal]
        [:div.app-container
         [display-overlay current-modal]
         [display-modal current-modal]
         [navbar/root]
         [show-panel @active-panel]]))))
