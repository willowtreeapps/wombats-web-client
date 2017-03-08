(ns wombats-web-client.events.bootstrap
  (:require [ajax.core :refer [GET]]
            [cljs.core.async :as async]
            [re-frame.core :as re-frame]
            [ajax.edn :refer [edn-response-format]]
            [wombats-web-client.events.user :refer [load-wombats]]
            [wombats-web-client.db :refer [default-db]]
            [wombats-web-client.socket-dispatcher :as sd]
            [wombats-web-client.events.spritesheet :refer [get-spritesheet]]
            [wombats-web-client.utils.local-storage :refer [remove-token!]]
            [wombats-web-client.utils.bootstrap :refer [bootstrap-failure 
                                                        token-from-url
                                                        redirect-unauthenticated]]
            [wombats-web-client.constants.urls :refer [self-url]]
            [wombats-web-client.socket-dispatcher :as sd]
            [wombats-web-client.utils.auth :refer [add-auth-header]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(re-frame/reg-event-db
 :set-current-user
 (fn [db [_ current-user]]
   (assoc db :current-user current-user)))

(re-frame/reg-event-db
 :initialize-db
 (fn [_ _]
   ;; Check to see if token is in the url, if so assoc it into the db
   (let [token (token-from-url)]
     (if token
       (assoc default-db :auth-token token)
       default-db))))

(re-frame/reg-event-db
 :bootstrap-complete
 (fn [db [_ _]]
   (assoc db :bootstrapping? false)))

(defn load-user-success [{:keys [user/id] :as current-user}]
  (re-frame/dispatch-sync [:set-current-user current-user])

  ;; Connect to web socket (and polling),
  ;; get spritesheet, and fetch wombats.
  (let [socket-ch (sd/init-ws-connection)
        sprite-ch (get-spritesheet)
        wombat-ch (load-wombats id)]
    (go
      (let [socket (async/<! socket-ch)]
        (if socket
          (sd/socket-polling)
          (bootstrap-failure)))
  
      (let [sprite (async/<! sprite-ch)]
        (if sprite
          (re-frame/dispatch [:update-spritesheet sprite])
          (bootstrap-failure)))

      (let [wombats (async/<! wombat-ch)]
        (if wombats
          (re-frame/dispatch [:update-wombats wombats])
          (bootstrap-failure)))
      
      ;; Update bootstrapping in db
      (re-frame/dispatch [:bootstrap-complete]))))

(defn bootstrap-user   
  "fetches the current user" 
  []
  (GET self-url {:response-format (edn-response-format)
                 :keywords? true
                 :headers (add-auth-header {})
                 :handler load-user-success
                 :error-handler bootstrap-failure}))

(defn bootstrap []
  ;; First check to see if token exists in db
  (let [token @(re-frame/subscribe [:auth-token])]
    (if token
      (bootstrap-user)
      (do
        (re-frame/dispatch [:bootstrap-complete])
        (redirect-unauthenticated)))))
