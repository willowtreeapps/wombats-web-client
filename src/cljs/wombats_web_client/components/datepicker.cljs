(ns wombats-web-client.components.datepicker
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.utils.forms :refer [get-value]]
            [wombats-web-client.utils.errors :refer [required-field-error]]
            [wombats-web-client.components.inline-error :refer [inline-error]]
            [cljs-time.core :as t]
            [cljs-time.format :as f]))

(defn check-for-valid-date [state error-key value]
  (when (clojure.string/blank? value)
    (swap! state assoc error-key required-field-error)))

(defn on-date-change [state key error-key value]
  (swap! state assoc error-key nil key value))

(def time-formatter
  (f/formatter "YYYY-MM-dTHH:mm"))

(defn format-time [time-object]
  (f/unparse time-formatter
             time-object))

(defn datepicker
  [{:keys [class name label state]}]
  (let [name-key (keyword name)
        error-key (keyword (str name "-error"))
        val (name-key @state)
        error-val (error-key @state)
        current-time (t/local-date-time (t/now))]
    (println (format-time current-time))
    (println "2015-01-02T11:42")
    [:div.datepicker-wrapper {:class class}
     [:label.label {:for name} label]
     [:input.input {:class (when error-val "field-error")
                    :type "datetime-local"
                    :defaultValue (format-time current-time)
                    :on-blur #(check-for-valid-date
                               state
                               error-key
                               (get-value %))
                    :on-change #(on-date-change
                                 state
                                 name-key
                                 error-key
                                 (get-value %))}]
     [inline-error error-val]]))
