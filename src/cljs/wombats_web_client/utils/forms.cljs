(ns wombats-web-client.utils.forms
  (:require [re-frame.core :as re-frame]))


(defn get-value [element]
  (-> element .-target .-value))

(defn check-for-valid-content [state error-key value]
  (if (clojure.string/blank? value)
    (swap! state assoc error-key "This field is required")
    (swap! state assoc error-key nil)))

(defn on-text-change [state key error-key value]
  (swap! state assoc error-key nil key value))

;; name must match local component state key
(defn text-input-with-label
  [{:keys [name label state is-password]}]
  (let [key (keyword name)
        error-key (keyword (str name "-error"))
        val (get @state key)
        error-val (get @state error-key)]
    [:div.text-input-wrapper
     [:label.label {:for name} label]
     [:input.input {:class (when error-val "field-error")
                    :type (if is-password "password" "text")
                    :name name
                    :value (when val val)
                    :on-blur #(check-for-valid-content state error-key (get-value %))
                    :on-change #(on-text-change state key error-key (get-value %))}]
     (when error-val
       [:div.inline-error error-val])]))

(defn cancel-modal-input []
  [:input.modal-button {:type "button"
                               :value "CANCEL"
                        :on-click (fn []
                                    (re-frame/dispatch [:set-modal nil]))}])
