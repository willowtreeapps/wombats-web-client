(ns wombats-web-client.utils.forms
  (:require [re-frame.core :as re-frame]))


(defn get-value [element]
  (-> element .-target .-value))

(defn check-for-valid-content [state error-key value]
  (if (clojure.string/blank? value)
    (swap! state assoc error-key "Field is required")
    (swap! state assoc error-key nil)))

;; name must match local component state key
(defn text-input-with-label
  [{:keys [name label state]}]
  (let [key (keyword name)
        error-key (keyword (str name "-error"))
        val (get @state key)
        error-val (get @state error-key)]
    [:div.text-input-wrapper
     [:label.label {:for name} label]
     [:input.input {:class (when error-val "field-error")
                    :type "text"
                    :name name
                    :value (when val val)
                    :on-blur #(check-for-valid-content state error-key (get-value %))
                    :on-change #(swap! state assoc key (get-value %))}]
     (when error-val
       [:div.inline-error error-val])]))

(defn cancel-modal-input []
  [:input.modal-button {:type "button"
                               :value "CANCEL"
                        :on-click (fn []
                                    (re-frame/dispatch [:set-modal nil]))}])
