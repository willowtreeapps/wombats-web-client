(ns wombats-web-client.components.access-key-table
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.utils.time :refer [format-local-date]]
            [wombats-web-client.components.add-button :as add-button]
            [wombats-web-client.components.table :refer [table]]
            [wombats-web-client.components.modals.create-access-key-modal
             :refer [create-access-key-modal]]))

(defonce headers ["Name"
                  "Description"
                  "Keys Left"
                  "Expiration Date"])

(defn open-create-access-key-modal []
  (re-frame/dispatch [:set-modal {:fn #(create-access-key-modal)
                                  :show-overlay true}]))
(defn get-items-fn [row-data]
  (let [{:keys [:access-key/key
                :access-key/description
                :access-key/max-number-of-uses
                :access-key/number-of-uses
                :access-key/expiration-date]} row-data
                keys-left (- max-number-of-uses number-of-uses)
                formatted-date (format-local-date expiration-date)]

    [key description keys-left formatted-date]))

(defn access-key-table [keys]
  (fn [keys]
    [:div.access-key-display
     [add-button/root open-create-access-key-modal "add-access-key"]
     [table "access-key-table" headers @keys get-items-fn]]))
