(ns wombats-web-client.components.modals.game-started-modal
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.utils.errors :refer [game-started-error]]
            [wombats-web-client.utils.forms :refer [submit-modal-input]]))

(defn close-modal []
  (re-frame/dispatch [:set-modal nil]))

(defn game-started-modal []
  (fn []
    [:div.modal.game-started-modal
     [:div.title "GAME HAS STARTED"]
     [:div.modal-content
      [:div.desc game-started-error]]
     [:div.action-buttons
      [submit-modal-input "OKAY" close-modal]]]))
