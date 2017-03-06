(ns wombats-web-client.components.join-button
  (:require [reagent.core :as reagent]))

(defn join-button
  [{:keys [is-private on-click]}]
  [:button.join-button {:class (when is-private "private")
                        :on-click on-click}
   "JOIN"])