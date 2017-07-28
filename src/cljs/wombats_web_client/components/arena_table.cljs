(ns wombats-web-client.components.arena-table
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.components.add-button :as add-button]
            [wombats-web-client.components.table :refer [table]]
            [wombats-web-client.components.modals.arena-modal
             :refer [arena-modal]]
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

(defn open-add-arena-modal []
  (re-frame/dispatch [:set-modal {:fn #(arena-modal)
                                  :show-overlay true}]))

(defn open-edit-arena-modal [row-data]
  (re-frame/dispatch [:set-modal {:fn #(arena-modal row-data)
                                  :show-overlay true}]))

(defn edit-arena-button [row-data]
  [:input.edit-arena {:type "button"
                      :value "EDIT"
                      :on-click #(open-edit-arena-modal row-data)}])

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
        edit-arena (edit-arena-button row-data)
        create-game (create-game-button id)]
    [name
     dimension
     food
     poison
     steel-walls
     wood-walls
     zakano
     edit-arena
     create-game]))

(defn arena-table [arenas]
  [:div.arena-display
   [add-button/root open-add-arena-modal "add-arena"]
   [table "arena-table" headers @arenas get-items-fn]])
