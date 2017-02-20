(ns wombats-web-client.views
    (:require [re-frame.core :as re-frame]
              
              ;; Components
              [wombats-web-client.components.navbar :as navbar]

              ;; Panels
              [wombats-web-client.panels.games :as view-games-panel]
              [wombats-web-client.panels.account :as account-panel]
              [wombats-web-client.panels.game-play :as game-play-panel]))

;; mainutil

(defn- panels [panel-name]
  (case panel-name
    :view-games-panel [view-games-panel/games]
    :account-panel [account-panel/account]
    :game-play-panel [game-play-panel/game-play]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn display-special-modal [special-modal]
  [:div {:class "modal-container"}
   [special-modal]])

(defn display-modal
  [modal]
  [:div {:class-name "modal-container"}
   [:div {:class-name "modal-overlay"}]
   [modal]])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])
        modal (re-frame/subscribe [:modal])
        special-modal (re-frame/subscribe [:special-modal])]
    (fn []
      (let [modal @modal
            special-modal @special-modal]
        [:div.app-container
         (when modal [display-modal modal])
         (when special-modal [display-special-modal special-modal])
         [navbar/root]
         [show-panel @active-panel]]))))
