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
 :simulator/update-simulator-templates
 (fn [db [_ templates]]
   (assoc-in db [:simulator/templates] templates)))

(re-frame/reg-event-db
 :simulator/update-code
 (fn [db [_ code]]
   (let [player-id (first (keys (get-in db [:simulator/state :players])))]
     (assoc-in db [:simulator/state :players player-id :state :code :code] code))))

(re-frame/reg-event-db
 :simulator/update-state
 (fn [db [_ sim-state]]
   (assoc db :simulator/state sim-state)))

(re-frame/reg-event-db
 :simulator/update-active-simulator-pane
 (fn [db [_ active-pane]]
   (assoc db :simulator/active-pane active-pane)))

(re-frame/reg-event-db
 :simulator/update-configuration
 (fn [db [_ {wombat-id :wombat-id
            template-id :template-id}]]
   (merge db {:simulator/template-id template-id
              :simulator/wombat-id wombat-id})))
