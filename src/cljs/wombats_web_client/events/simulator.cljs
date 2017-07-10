(ns wombats-web-client.events.simulator
  (:require [ajax.core :refer [GET]]
            [ajax.edn :refer [edn-request-format edn-response-format]]
            [re-frame.core :as re-frame]
            [wombats-web-client.constants.urls :refer [simulator-templates-url]]
            [wombats-web-client.utils.auth :refer [add-auth-header]]
            [wombats-web-client.utils.games :as games]
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
   (let [mini-map (get-in (games/get-player db) [:state :mini-map])]
     (assoc db :simulator/state sim-state
            :simulator/frames-vec
            (conj (:simulator/frames-vec db) sim-state)
            :simulator/frames-vec-mini-map
            (conj (:simulator/frames-vec-mini-map db) mini-map)
            :simulator/frames-idx
            (inc (:simulator/frames-idx db))))))

(re-frame/reg-event-db
 :simulator/back-frame
 (fn [db _] ;; TODO this needs to check if the length of the frames vec will allow for a decrement
   (update db :simulator/frames-idx dec)))

(re-frame/reg-event-db
 :simulator/forward-frame
 (fn [db _] ;; TODO this needs to check if the length of the frames vec will allow for an increment
   (update db :simulator/frames-idx inc)))

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


;; TODO merge these two functions common functionality
(re-frame/reg-event-db
 :simulator/update-configuration
 (fn [db [_ {wombat-id :wombat-id
            template-id :template-id}]]
   (merge db {:simulator/template-id template-id
              :simulator/wombat-id wombat-id})))

(re-frame/reg-event-db
 :simulator/initialize-configuration
 (fn [db [_ {wombat-id :wombat-id
            template-id :template-id}]]
   (merge db {:simulator/template-id template-id
              :simulator/wombat-id wombat-id
              :simulator/frames-vec []
              :simulator/frames-vec-mini-map []
              :simulator/frames-idx 0})))

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
    :dispatch [:simulator/initialize-configuration simulation-payload]}))

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
