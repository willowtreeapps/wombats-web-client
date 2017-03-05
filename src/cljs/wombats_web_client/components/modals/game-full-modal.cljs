(ns wombats-web-client.components.modals.game-full-modal
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.events.games :refer [get-all-games]]
            [wombats-web-client.utils.errors :refer [game-full-error]]))

(defn close-modal []
  (get-all-games)
  (re-frame/dispatch [:set-modal nil]))

(defn game-full-modal []
  (fn []
    [:div.modal.game-full-modal
     [:div.title "GAME FULL"]
     [:div.desc game-full-error]
     [:div.action-buttons
      [:button.close-button {:on-click close-modal} "OKAY"]]]))
