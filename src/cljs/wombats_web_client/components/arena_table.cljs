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
  (re-frame/dispatch [:set-modal {:fn #(create-game-modal arena-id)
                                  :show-overlay true}]))

(defn create-game-button [arena-id]
  [:input.create-game {:type "button"
                       :value "CREATE GAME"
                       :on-click #(open-create-game-modal arena-id)}])

(defn get-items-fn [row-data]
  (let [{:keys [:arena/width
                :arena/height
                :arena/food
                :arena/poison
                :arena/steel-walls
                :arena/wood-walls
                :arena/zakano
                :arena/id
                :arena/name]} row-data
                dimension (str width "x" height)
                create-game (create-game-button id)]
    [name
     dimension
     food
     poison
     steel-walls
     wood-walls
     zakano
     create-game]))

(defn arena-table [arenas]
  [:div.arena-display
   [table "arena-table" headers @arenas get-items-fn]])
