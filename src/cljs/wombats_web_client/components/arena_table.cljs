(ns wombats-web-client.components.arena-table
  (:require [wombats-web-client.components.table :refer [table]]))

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

(defn arena-table [arenas]
  [table "arena-table" headers @arenas get-items-fn])
