(ns wombats-web-client.utils.forms
  (:require [re-frame.core :as re-frame]))

(defn text-input-with-label
  [{:keys [name label local-state-value]}]
  [:div.text-input-wrapper
   [:label.label {:for name} label]
   [:input.input {:type "text"
                  :name name
                  :on-change #(reset! local-state-value (-> % .-target .-value))}]])

(defn cancel-modal-input []
  [:input.modal-button {:type "button"
                               :value "CANCEL"
                               :on-click #(re-frame/dispatch [:set-modal nil])}])
