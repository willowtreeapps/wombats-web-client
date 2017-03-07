(ns wombats-web-client.components.add-button)

(defn root
  [onClick]
  (fn [onClick]
    [:a.add-button {:on-click onClick}
     [:div.text "+"]]))
