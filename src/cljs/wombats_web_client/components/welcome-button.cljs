(ns wombats-web-client.components.welcome-button)

(defn root
  [onClick]
  (fn [onClick]
    [:button.welcome-button {:type "submit" :on-click onClick}
     [:span.welcome-button-label "start playing now"]]))