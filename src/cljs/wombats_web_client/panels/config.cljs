(ns wombats-web-client.panels.config
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.components.header :refer [header]]
            [wombats-web-client.components.table :refer [table]]
            [wombats-web-client.events.arenas :refer [get-arenas]]))

(defonce headers ["Name"
                  "Grid"
                  "Food"
                  "Poison"
                  "Steel Barrier"
                  "Wood Barrier"
                  "Zakano"])

(defn get-items-fn [row-data]
  (let [name (:arena/name row-data)
        width (:arena/width row-data)
        height (:arena/height row-data)
        food (:arena/food row-data)
        poison (:arena/poison row-data)
        steel (:arena/steel-walls row-data)
        wood (:arena/wood-walls row-data)
        zakano (:arena/zakano row-data)
        dimension (str width "x" height)]
    [name dimension food poison steel wood zakano]))

(defn config []
  (get-arenas)

  (let [arenas (re-frame/subscribe [:arenas])]
    (fn []
      [:div.config-panel
       [header "ARENAS"]
       [table headers @arenas get-items-fn]])))
