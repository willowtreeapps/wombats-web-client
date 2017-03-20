(ns wombats-web-client.components.add-button)

(defn root
  [onClick classname]
  [:a.add-button {:class classname
                  :on-click onClick}
   [:div.text "+"]])
