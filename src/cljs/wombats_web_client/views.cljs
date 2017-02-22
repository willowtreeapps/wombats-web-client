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

(defn display-modal
  [modal]
  (let [render-fn (:fn modal)
        show-overlay? (:show-overlay? modal)
        visibility (if modal "visible" "hidden")]
    [:div {:class-name "modal-container"
           :style {:visibility visibility}}
     (when show-overlay? [:div {:class-name "modal-overlay"}])
     (when render-fn [render-fn])]))

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])
        modal (re-frame/subscribe [:modal])]
    (fn []
      (let [modal @modal]
        [:div.app-container
         [display-modal modal]
         [navbar/root]
         [show-panel @active-panel]]))))
