(ns wombats-web-client.views
    (:require [re-frame.core :as re-frame]

              ;; Components
              [wombats-web-client.components.navbar :as navbar]

              ;; Panels
              [wombats-web-client.panels.games :as view-games-panel]
              [wombats-web-client.panels.account :as account-panel]
              [wombats-web-client.panels.game-play :as game-play-panel]
              [wombats-web-client.panels.welcome :as welcome-panel]
              [wombats-web-client.panels.simulator :as simulator-panel]
              [wombats-web-client.panels.page-not-found :as page-not-found-panel]

              [wombats-web-client.utils.local-storage :refer [get-token]]))

;; mainutil

(defn- panels [panel-name params]
  (case panel-name
    :view-games-panel [view-games-panel/games params]
    :account-panel [account-panel/account params]
    :game-play-panel [game-play-panel/game-play params]
    :welcome-panel [welcome-panel/welcome params]
    :simulator-panel [simulator-panel/simulator params]
    :page-not-found-panel [page-not-found-panel/page-not-found params]
    [:div]))

(defn show-panel [{:keys [panel-id params]}]
  [panels panel-id params])

(defn display-modal
  [modal]
  (let [render-fn (:fn modal)
        show-overlay? (:show-overlay? modal)
        visibility (if modal "visible" "hidden")]
    [:div {:class-name "modal-container"
           :style {:visibility visibility}}
     (when show-overlay? [:div {:class-name "modal-overlay"}])
     (when render-fn [render-fn])]))

(defn logged-in-view [modal panel]
  [:div.app-container
   [display-modal modal]
   [navbar/root]
   [show-panel panel]])

(defn logged-out-view [panel]
  [:div.app-container
   [show-panel panel]])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])
        auth-token (re-frame/subscribe [:auth-token])
        modal (re-frame/subscribe [:modal])]
    (fn []
      (let [modal @modal
            token @auth-token
            panel @active-panel]
        (if (and token (not= panel :welcome-panel)) 
          [logged-in-view modal panel]
          [logged-out-view panel])))))
