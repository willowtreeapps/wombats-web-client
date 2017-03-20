(ns wombats-web-client.panels.config
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.components.header :refer [header]]
            [wombats-web-client.components.arena-table
             :refer [arena-table]]
            [wombats-web-client.events.arenas :refer [get-arenas]]))

(defn config []
  (get-arenas)

  (let [arenas (re-frame/subscribe [:arenas])]
    (fn []
      [:div.config-panel
       [header "ARENAS"]
       [arena-table arenas]])))
