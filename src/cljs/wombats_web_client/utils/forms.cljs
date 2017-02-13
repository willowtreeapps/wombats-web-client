(ns wombats-web-client.utils.forms
  (:require [re-frame.core :as re-frame]))


;; name must match local component state key
(defn text-input-with-label
  [{:keys [name label state]}]
  (let [val (get @state (keyword name))]
    [:div.text-input-wrapper
     [:label.label {:for name} label]
     [:input.input {:type "text"
                    :name name
                    :value (when val val)
                    :on-change #(swap! state assoc (keyword name) (-> % .-target .-value))}]]))

(defn cancel-modal-input []
  [:input.modal-button {:type "button"
                               :value "CANCEL"
                        :on-click (fn []
                                    (re-frame/dispatch [:set-modal nil]))}])
