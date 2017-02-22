(ns wombats-web-client.components.modals.game-full-modal
  (:require [re-frame.core :as re-frame]))

(defonce full-msg "This game is aready full. Please try joining another game.")

(defn game-full-modal []
  (fn []
    [:div {:class "modal game-full-modal"}
     [:div.title "GAME FULL"]
     [:div.desc full-msg]
     [:div.action-buttons
      [:button.simple-button {:on-click #(re-frame/dispatch [:set-modal nil])} "OKAY"]]]))
