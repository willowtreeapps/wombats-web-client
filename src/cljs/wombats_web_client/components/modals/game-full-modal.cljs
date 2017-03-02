(ns wombats-web-client.components.modals.game-full-modal
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.events.games :refer [get-all-games]]))

(defonce full-msg "This game is aready full. Please try joining another game.")

(defn close-modal []
  (get-all-games)
  (re-frame/dispatch [:set-modal nil]))

(defn game-full-modal []
  (fn []
    [:div {:class "modal game-full-modal"}
     [:div.title "GAME FULL"]
     [:div.desc full-msg]
     [:div.action-buttons
      [:button.simple-button {:on-click close-modal} "OKAY"]]]))
