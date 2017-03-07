(ns wombats-web-client.components.inline-error)

(defn inline-error [value]
  (when value
    [:div.inline-error value]))
