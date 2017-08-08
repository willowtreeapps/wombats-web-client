(ns wombats-web-client.components.modals.arena-modal
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]

            [wombats-web-client.components.text-input
             :refer [text-input-with-label]]
            [wombats-web-client.components.datepicker
             :refer [datepicker]]
            [wombats-web-client.components.radio-select
             :refer [radio-select]]
            [wombats-web-client.events.arenas
             :refer [get-arenas
                     edit-arena
                     create-arena]]
            [wombats-web-client.utils.errors :refer [get-error-message
                                                     required-field-error
                                                     not-an-integer
                                                     max-twenty-five
                                                     min-five]]
            [wombats-web-client.utils.forms
             :refer [cancel-modal-input
                     submit-modal-input
                     input-error!]]
            [wombats-web-client.utils.functions :refer [no-blanks?]]
            [wombats-web-client.utils.time :refer [local-time-to-utc]]))

(defonce perimeter-radios ["on" "off"])
(defonce initial-state {:arena/name nil
                        :arena/name-error nil
                        :arena/width nil
                        :arena/width-error nil
                        :arena/height nil
                        :arena/height-error nil
                        :arena/perimeter (first perimeter-radios)
                        :arena/shot-damage nil
                        :arena/smoke-duration nil
                        :arena/food nil
                        :arena/poison nil
                        :arena/steel-walls nil
                        :arena/wood-walls nil
                        :arena/zakano nil
                        :arena/steel-wall-hp 500
                        :arena/zakano-hp 50
                        :arena/wombat-hp 200
                        :arena/wood-wall-hp 30})

(defn callback-success []
  (get-arenas)
  (re-frame/dispatch [:update-modal-error nil])
  (re-frame/dispatch [:set-modal nil]))

(def callback-error
  (fn [error cmpnt-state]
    (re-frame/dispatch [:update-modal-error (get-error-message error)])
    (reset! cmpnt-state initial-state)))

(defn check-for-errors [cmpnt-state]
  (let [validation-checks
        [{:key-name :arena/name
          :error required-field-error}
         {:key-name :arena/width
          :error not-an-integer}
         {:key-name :arena/width
          :error max-twenty-five}
         {:key-name :arena/width
          :error min-five}
         {:key-name :arena/width
          :error required-field-error}
         {:key-name :arena/height
          :error not-an-integer}
         {:key-name :arena/height
          :error max-twenty-five}
         {:key-name :arena/height
          :error min-five}
         {:key-name :arena/height
          :error required-field-error}
         {:key-name :arena/perimeter
          :error required-field-error}
         {:key-name :arena/shot-damage
          :error not-an-integer}
         {:key-name :arena/smoke-duration
          :error not-an-integer}
         {:key-name :arena/food
          :error required-field-error}
         {:key-name :arena/food
          :error not-an-integer}
         {:key-name :arena/poison
          :error not-an-integer}
         {:key-name :arena/steel-walls
          :error not-an-integer}
         {:key-name :arena/wood-walls
          :error not-an-integer}
         {:key-name :arena/zakano
          :error not-an-integer}]]
    (doall (map #(input-error! % cmpnt-state) validation-checks))))

(defn get-perimeter-bool
  [perimeter-status]
  (= perimeter-status "on"))

(defn get-perimeter-string
  [perimeter-bool]
  (if perimeter-bool
    "on"
    "off"))

(defn- map-map-val
  [m f]
  (into {} (for [[k v] m] [k (f v)])))

(defn- all-integers?
  [m]
  (every? true? (map #(integer? (js/parseInt %))
                      (vals m))))

(defn- filter-arena-fields
  [m]
  (select-keys m
               [:arena/id
                :arena/perimeter
                :arena/name
                :arena/width
                :arena/height
                :arena/shot-damage
                :arena/smoke-duration
                :arena/food
                :arena/poison
                :arena/steel-walls
                :arena/wood-walls
                :arena/zakano
                :arena/wood-wall-hp
                :arena/steel-wall-hp
                :arena/zakano-hp
                :arena/wombat-hp]))

(defn on-submit-form-valid [cmpnt-state]
  (let [test-data (filter-arena-fields @cmpnt-state)
        integer-data (map-map-val (dissoc test-data
                                          :arena/id
                                          :arena/perimeter
                                          :arena/name) #(js/parseInt %))
        perimeter-bool {:arena/perimeter
                        (get-perimeter-bool (:arena/perimeter test-data))}
        not-blank (no-blanks? (vals test-data))
        all-integers (all-integers? integer-data)
        ready-to-submit (and not-blank all-integers)
        handlers {:on-success #(callback-success)
                  :on-error #(callback-error % cmpnt-state)}
        submission-data (merge test-data integer-data perimeter-bool handlers)]

    (check-for-errors cmpnt-state)
    (when ready-to-submit
      (if (:arena/id test-data)
        (edit-arena submission-data)
        (create-arena submission-data)))))

(defn arena-modal
  ([] (arena-modal {}))
  ([row-data]
   (let [arena-data (filter-arena-fields row-data)
         perimeter-walls {:arena/perimeter
                          (get-perimeter-string
                           (:arena/perimeter arena-data))}
         modal-error (re-frame/subscribe [:modal-error])
         cmpnt-state (reagent/atom (merge
                                    initial-state
                                    arena-data
                                    perimeter-walls))
         title (if (:arena/id arena-data) "EDIT ARENA" "CREATE ARENA")
         confirm (if (:arena/id arena-data) "SAVE" "CREATE")]
     (fn []
       [:div.modal.create-arena-modal
        [:div.title title]
        (when @modal-error [:div.modal-error @modal-error])
        [:div.modal-content
         [text-input-with-label {:class "name"
                                 :name "arena/name"
                                 :label "Arena Name"
                                 :state cmpnt-state}]
         [radio-select {:class "perimeter"
                        :name "arena/perimeter"
                        :label "Perimeter Walls"
                        :state cmpnt-state
                        :radios perimeter-radios}]
         [text-input-with-label {:class "width"
                                 :name "arena/width"
                                 :label "Width"
                                 :state cmpnt-state}]
         [text-input-with-label {:class "height"
                                 :name "arena/height"
                                 :label "Height"
                                 :state cmpnt-state}]
         [text-input-with-label {:class "shot-damage"
                                 :name "arena/shot-damage"
                                 :label "Shot Damage"
                                 :state cmpnt-state}]
         [text-input-with-label {:class "smoke-duration"
                                 :name "arena/smoke-duration"
                                 :label "Smoke Duration"
                                 :state cmpnt-state}]
         [text-input-with-label {:class "food"
                                 :name "arena/food"
                                 :label "Food"
                                 :state cmpnt-state}]
         [text-input-with-label {:class "poison"
                                 :name "arena/poison"
                                 :label "Poison"
                                 :state cmpnt-state}]
         [text-input-with-label {:class "steel-walls"
                                 :name "arena/steel-walls"
                                 :label "Steel Barriers"
                                 :state cmpnt-state}]
         [text-input-with-label {:class "wood-walls"
                                 :name "arena/wood-walls"
                                 :label "Wood Barriers"
                                 :state cmpnt-state}]
         [text-input-with-label {:class "zakano"
                                 :name "arena/zakano"
                                 :label "Zakano"
                                 :state cmpnt-state}]]

        [:div.action-buttons
         [cancel-modal-input]
         [submit-modal-input
          confirm
          #(on-submit-form-valid cmpnt-state)]]]))))
