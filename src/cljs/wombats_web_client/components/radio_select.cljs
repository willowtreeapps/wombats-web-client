(ns wombats-web-client.components.radio-select)

(defn radio-select
  [{:keys [class name label state]}]
  (let [key (keyword name)
        error-key (keyword (str name "-error"))
        val (get @state key)
        error-val (get @state error-key)]
    [:div.radio-select-wrapper {:class class}
     [:label.label {:for name} label]]))
