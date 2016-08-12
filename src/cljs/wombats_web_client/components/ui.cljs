(ns wombats_web_client.components.ui
  (:require [re-frame.core :as re-frame]))

(defn render-modal
  [modal]
  (if modal
    [:div.modal-container
     [:div.modal-component
      [:button.clear-modal-btn {:on-click #(re-frame/dispatch [:clear-modal])} "X"]
      modal]]))
