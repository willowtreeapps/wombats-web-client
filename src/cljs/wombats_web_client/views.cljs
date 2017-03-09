(ns wombats-web-client.views
    (:require [re-frame.core :as re-frame]

              [pushy.core :as pushy]

              ;; Components
              [wombats-web-client.components.navbar :as navbar]

              ;; Panels
              [wombats-web-client.panels.games :as view-games-panel]
              [wombats-web-client.panels.account :as account-panel]
              [wombats-web-client.panels.game-play :as game-play-panel]
              [wombats-web-client.panels.welcome :as welcome-panel]
              [wombats-web-client.panels.simulator :as simulator-panel]
              [wombats-web-client.panels.page-not-found :as page-not-found-panel]
              [wombats-web-client.routes :refer [history]]

              [wombats-web-client.utils.local-storage :refer [get-token remove-token!]]))

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

(defn display-navbar [panel]
  (when (not= (:panel-id panel) :welcome-panel)
    [navbar/root]))

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])
        modal (re-frame/subscribe [:modal])
        bootstrapping? (re-frame/subscribe [:bootstrapping?])]
    (fn []
      (let [bootstrapping? @bootstrapping?
            modal @modal
            panel @active-panel]



         ;; If you're bootstrapping show loading
        (if bootstrapping?
          [:div.loading-app-container 
           [:p "Loading..."]
           [:img {:src "/images/naked_dancing_wombat.gif"}]]

          [:div.app-container
           [display-modal modal]
           [display-navbar panel]
           [show-panel panel]])))))
