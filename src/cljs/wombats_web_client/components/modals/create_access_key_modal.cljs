(ns wombats-web-client.components.modals.create-access-key-modal
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [wombats-web-client.utils.forms
             :refer [cancel-modal-input
                     submit-modal-input
                     input-error!]]
            [wombats-web-client.components.text-input
             :refer [text-input-with-label]]
            [wombats-web-client.components.datepicker
             :refer [datepicker]]
            [wombats-web-client.utils.time :refer [local-time-to-utc]]
            [wombats-web-client.utils.functions :refer [no-blanks?]]
            [wombats-web-client.utils.errors :refer [get-error-message
                                                     required-field-error
                                                     not-an-integer]]
            [wombats-web-client.events.access-key :refer [get-access-keys
                                                          create-access-key]]))

(defonce initial-state {:key-name nil
                        :key-name-error nil
                        :description nil
                        :description-error nil
                        :number-of-keys nil
                        :number-of-keys-error nil
                        :expiration-date nil
                        :expiration-date-error nil})

(defn callback-success []
  (get-access-keys)
  (re-frame/dispatch [:update-modal-error nil])
  (re-frame/dispatch [:set-modal nil]))

(def callback-error
  (fn [error cmpnt-state]
    (re-frame/dispatch [:update-modal-error (get-error-message error)])
    (reset! cmpnt-state initial-state)))

(defn check-for-errors [cmpnt-state]
  (let [validation-checks
        [{:key-name :key-name
          :test-fn clojure.string/blank?
          :error required-field-error}
         {:key-name :description
          :test-fn clojure.string/blank?
          :error required-field-error}
         {:key-name :number-of-keys
          :test-fn clojure.string/blank?
          :error required-field-error}
         {:key-name :number-of-keys
          :test-fn #(not
                     (integer? (js/parseInt %)))
          :error not-an-integer}
         {:key-name :expiration-date
          :test-fn clojure.string/blank?
          :error required-field-error}]]
    (doall (map #(input-error! % cmpnt-state) validation-checks))))

(defn on-submit-form-valid [cmpnt-state]
  (let [{:keys [key-name
                description
                number-of-keys
                expiration-date]} @cmpnt-state
                not-blank (no-blanks? [key-name
                                       description
                                       number-of-keys
                                       expiration-date])
                num-keys (js/parseInt number-of-keys)
                ready-to-submit (and (integer? num-keys)
                                     not-blank)]
    (check-for-errors cmpnt-state)
    (when ready-to-submit
      (let [expiration-utc (local-time-to-utc expiration-date)]
        (create-access-key {:key-name key-name
                            :max-num num-keys
                            :expiration expiration-utc
                            :desc description
                            :on-success #(callback-success)
                            :on-error #(callback-error % cmpnt-state)})))))

(defn create-access-key-modal []
  (let [modal-error (re-frame/subscribe [:modal-error])
        cmpnt-state (reagent/atom initial-state)]
    (fn []
      [:div.modal.create-access-key-modal
       [:div.title "CREATE ACCESS KEY"]
       (when @modal-error [:div.modal-error @modal-error])
       [:div.modal-content
        [text-input-with-label {:class "key-name"
                                :name "key-name"
                                :label "Key Name"
                                :state cmpnt-state}]
        [text-input-with-label {:class "description"
                                :name "description"
                                :label "Description"
                                :state cmpnt-state}]
        [text-input-with-label {:class "number-of-keys"
                                :name "number-of-keys"
                                :label "Number of Keys"
                                :state cmpnt-state}]
        [datepicker {:class "expiration-date"
                     :name "expiration-date"
                     :label "Expiration Date"
                     :state cmpnt-state}]]
       [:div.action-buttons
        [cancel-modal-input]
        [submit-modal-input
         "CREATE"
         #(on-submit-form-valid cmpnt-state)]]])))
