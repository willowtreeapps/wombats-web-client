(ns wombats-web-client.events.access-key
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.constants.urls :refer [access-key-url]]
            [ajax.edn :refer [edn-request-format edn-response-format]]
            [ajax.core :refer [GET]]
            [wombats-web-client.utils.auth :refer [add-auth-header]]))

(defn get-access-keys []
  (GET access-key-url {:response-format (edn-response-format)
                       :keywords? true
                       :format (edn-request-format)
                       :headers (add-auth-header {})
                       :handler #(re-frame/dispatch [:update-access-keys %])
                       :error-handler #(print "error on get-access-keys")}))

(re-frame/reg-event-db
 :update-access-keys
 (fn [db [_ keys]]
   (assoc db :access-keys keys)))
