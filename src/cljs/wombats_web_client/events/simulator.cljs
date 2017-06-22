(ns wombats-web-client.events.simulator
  (:require [ajax.core :refer [GET]]
            [ajax.edn :refer [edn-request-format edn-response-format]]
            [re-frame.core :as re-frame]
            [wombats-web-client.constants.urls :refer [simulator-templates-url]]
            [wombats-web-client.utils.auth :refer [add-auth-header]]
            [wombats-web-client.constants.urls
             :refer [initialize-simulator-url
                     process-simulator-frame-url]]))

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
   (let [player-id (first
                    (keys
                     (get-in db [:simulator/state :game/players])))]
     (assoc-in db [:simulator/state
                   :game/players
                   player-id
                   :state
                   :code
                   :code] code))))

(re-frame/reg-event-db
 :simulator/update-state
 (fn [db [_ sim-state]]
   (assoc db :simulator/state sim-state)))

(re-frame/reg-event-db
 :simulator/simulator-error
 (fn [db [_ error]]
   ;; TODO #279 Error State
   (assoc db :simulator/error error)))

(re-frame/reg-event-db
 :simulator/show-arena-view
 (fn [db _]
   (assoc db :simulator/view-mode :arena)))

(re-frame/reg-event-db
 :simulator/show-wombat-view
 (fn [db _]
   (assoc db :simulator/view-mode :self)))

(re-frame/reg-event-db
 :simulator/update-configuration
 (fn [db [_ {wombat-id :wombat-id
            template-id :template-id}]]
   (merge db {:simulator/template-id template-id
              :simulator/wombat-id wombat-id})))

(re-frame/reg-event-fx
 :simulator/initialize-simulator
 (fn [_ [_ simulation-payload]]
   {:http-xhrio {:method :post
                 :uri initialize-simulator-url
                 :params simulation-payload
                 :headers (add-auth-header {})
                 :format (edn-request-format)
                 :response-format (edn-response-format)
                 :on-success [:simulator/update-state]
                 :on-failure [:simulator/simulator-error]}
    :dispatch [:simulator/update-configuration simulation-payload]}))

(re-frame/reg-event-fx
 :simulator/process-simulation-frame
 (fn [_ [_ game-state]]
   {:http-xhrio {:method :post
                 :uri process-simulator-frame-url
                 :params game-state
                 :headers (add-auth-header {})
                 :format (edn-request-format)
                 :response-format (edn-response-format)
                 :on-success [:simulator/update-state]
                 :on-failure [:simulator/simulator-error]}}))
