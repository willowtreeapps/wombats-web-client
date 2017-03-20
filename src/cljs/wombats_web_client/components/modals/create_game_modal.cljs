(ns wombats-web-client.components.modals.create-game-modal
  (:require [reagent.core :as reagent]
            [wombats-web-client.utils.forms :refer [cancel-modal-input]]
            [wombats-web-client.components.text-input
             :refer [text-input-with-label]]
            [wombats-web-client.components.datepicker
             :refer [datepicker]]
            [wombats-web-client.components.radio-select
             :refer [radio-select]]))

(defn create-game-modal [arena-id]
  (let [cmpnt-state (reagent/atom {:game-name nil
                                   :game-name-error nil
                                   :number-of-players nil
                                   :number-of-players-error nil
                                   :number-of-rounds nil
                                   :number-of-rounds-error nil
                                   :intermission-time "01:10:00"
                                   :intermission-time-error nil
                                   :start-time nil
                                   :start-time-error nil})]
    [:div.modal.create-game-modal
     [:div.title "CREATE GAME"]
     [:div.modal-content
      [text-input-with-label {:class "game-name"
                              :name "game-name"
                              :label "Game Name"
                              :state cmpnt-state}]
      [text-input-with-label {:class "number-of-players"
                              :name "number-of-players"
                              :label "Number of Players"
                              :state cmpnt-state}]
      [text-input-with-label {:class "number-of-rounds"
                              :name "number-of-rounds"
                              :label "Number of Rounds"
                              :state cmpnt-state}]
      [text-input-with-label {:class "intermission-time"
                              :name "intermission-time"
                              :label "Intermission Time"
                              :state cmpnt-state}]
      [datepicker {:class "start-time"
                   :name "start-time"
                   :label "Start Time"
                   :state cmpnt-state}]
      [radio-select {:class "game-status"
                     :name "game-status"
                     :label "Game Status"
                     :state cmpnt-state}]]
     [:div.action-buttons
      [cancel-modal-input]]]))
