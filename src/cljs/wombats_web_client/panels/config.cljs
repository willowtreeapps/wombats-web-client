(ns wombats-web-client.panels.config
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.components.header :refer [header]]
            [wombats-web-client.components.arena-table
             :refer [arena-table]]
            [wombats-web-client.components.access-key-table
             :refer [access-key-table]]
            [wombats-web-client.events.arenas :refer [get-arenas]]
            [wombats-web-client.events.access-key :refer [get-access-keys]]))

(defn config []
  (let [arenas (re-frame/subscribe [:arenas])
        keys (re-frame/subscribe [:access-keys])]
    (reagent/create-class
     {:component-did-mount (fn []
                             (get-arenas)
                             (get-access-keys))
      :reagent-render
      (fn []
        [:div.config-panel
         [header "ARENAS"]
         [arena-table arenas]
         [header "ACCESS KEYS"]
         [access-key-table keys]])})))
