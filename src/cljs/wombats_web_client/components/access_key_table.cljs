(ns wombats-web-client.components.access-key-table
  (:require [re-frame.core :as re-frame]
           [wombats-web-client.components.table :refer [table]]))

(defonce headers ["Name"
                  "Description"
                  "Keys Left"
                  "Expiration Date"])

(defn get-items-fn [row-data]
  (let [{:keys [:access-key/key
                :access-key/description
                :access-key/max-number-of-uses
                :access-key/number-of-uses
                :access-key/expiration-date]} row-data
                keys-left (- max-number-of-uses number-of-uses)
                date (str expiration-date)]
    [key description keys-left date]))

(defn access-key-table [keys]
  [:div.access-key-display
   [table "access-key-table" headers @keys get-items-fn]])
