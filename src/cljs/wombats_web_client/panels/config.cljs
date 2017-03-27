(ns wombats-web-client.panels.config
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.components.header :refer [header]]
            [wombats-web-client.components.arena-table
             :refer [arena-table]]
            [wombats-web-client.events.arenas :refer [get-arenas]]))

(defn config []
  (let [arenas (re-frame/subscribe [:arenas])]
    (reagent/create-class
     {:component-did-mount #(get-arenas)
      :reagent-render
      (fn []
        [:div.config-panel
         [header "ARENAS"]
         [arena-table arenas]])})))
