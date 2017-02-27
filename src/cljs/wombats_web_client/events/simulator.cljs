(ns wombats-web-client.events.simulator
  (:require [ajax.core :refer [GET]]
            [ajax.edn :refer [edn-request-format edn-response-format]]
            [re-frame.core :as re-frame]
            [wombats-web-client.constants.urls :refer [simulator-templates-url]]
            [wombats-web-client.utils.auth :refer [add-auth-header]]
            [wombats-web-client.utils.socket :as ws]))

(defn- get-simulator-templates-request [on-success on-error]
  (GET simulator-templates-url {:response-format (edn-response-format)
                               :keywords? true
                               :format (edn-request-format)
                               :headers (add-auth-header {})
                               :handler on-success
                               :error-handler on-error}))

(defn get-simulator-templates []
  (get-simulator-templates-request
   #(re-frame/dispatch [:simulator/update-simulator-templates %])
   #(print "error on get-simulator-templates")))

(re-frame/reg-event-db
 :simulator/initialized
 (fn [db [_ initialized]]
   (assoc-in db [:simulator/initialized] initialized)))

(re-frame/reg-event-db
  :simulator/initialize
  (fn [db [_ payload]]
    (re-frame/dispatch [:simulator/initialized true])
    ;; TODO: Remove this
    (js/setTimeout #(ws/send-message :connect-to-simulator payload)
                   2000)
    db))

(re-frame/reg-event-db
 :simulator/update-simulator-templates
 (fn [db [_ templates]]
   (assoc-in db [:simulator/templates] templates)))

(re-frame/reg-event-db
 :simulator/update-code
 (fn [db [_ code]]
   (assoc-in db [:simulator/state] code)))

(re-frame/reg-event-db
 :simulator/update-state
 (fn [db [_ state]]
   (assoc db :simulator/state state)))
