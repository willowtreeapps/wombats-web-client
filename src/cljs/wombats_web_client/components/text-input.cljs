(ns wombats-web-client.components.text-input
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.utils.forms :refer [get-value]]
            [wombats-web-client.utils.errors :refer [required-field-error]]
            [wombats-web-client.components.inline-error :refer [inline-error]]))


(defn check-for-valid-text-content [state error-key value]
  (if (clojure.string/blank? value)
    (swap! state assoc error-key required-field-error)
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
                    :auto-complete (when is-password "new-password")
                    :name name
                    :value (when val val)
                    :on-blur #(check-for-valid-text-content state error-key (get-value %))
                    :on-change #(on-text-change state key error-key (get-value %))}]
     [inline-error error-val]]))
