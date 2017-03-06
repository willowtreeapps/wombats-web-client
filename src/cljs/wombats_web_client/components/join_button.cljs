(ns wombats-web-client.components.join-button)

(defn join-button
  [{:keys [is-private on-click]}]
  [:button.join-button {:class (when is-private "private")
                        :on-click on-click}
   "JOIN"])
