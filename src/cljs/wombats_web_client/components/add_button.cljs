(ns wombats-web-client.components.add-button)

(defn root
  [on-click classname]
  [:a.add-button {:class classname
                  :on-click on-click}
   [:div.text "+"]])
