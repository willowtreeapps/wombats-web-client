(ns wombats-web-client.components.modals.game-started-modal
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.utils.errors :refer [game-started-error]]))

(defn close-modal []
  (re-frame/dispatch [:set-modal nil]))

(defn game-started-modal []
  (fn []
    [:div {:class "modal game-started-modal"}
     [:div.title "GAME HAS STARTED"]
     [:div.desc game-started-error]
     [:div.action-buttons
      [:button {:class "close-button"
                :on-click close-modal} "OKAY"]]]))
