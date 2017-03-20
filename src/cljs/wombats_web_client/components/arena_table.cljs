(ns wombats-web-client.components.arena-table
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.components.table :refer [table]]
            [wombats-web-client.components.modals.create-game-modal
             :refer [create-game-modal]]))

(defonce headers ["Name"
                  "Grid"
                  "Food"
                  "Poison"
                  "Steel Barrier"
                  "Wood Barrier"
                  "Zakano"
                  ""])


(defn open-create-game-modal [arena-id]
  (fn []
    (re-frame/dispatch [:set-modal {:fn #(create-game-modal arena-id)
                                    :show-overlay true}])))

(defn create-game-button [arena-id]
  [:input.create-game {:type "button"
                       :value "CREATE GAME"
                       :on-click (open-create-game-modal arena-id)}])

(defn get-items-fn [row-data]
  (let [name (:arena/name row-data)
        width (:arena/width row-data)
        height (:arena/height row-data)
        food (:arena/food row-data)
        poison (:arena/poison row-data)
        steel (:arena/steel-walls row-data)
        wood (:arena/wood-walls row-data)
        zakano (:arena/zakano row-data)
        dimension (str width "x" height)
        id (:arena/id row-data)
        create-game (create-game-button id)]

    [name dimension food poison steel wood zakano create-game]))

(defn arena-table [arenas]
  [:div.arena-display
   [table "arena-table" headers @arenas get-items-fn]])
