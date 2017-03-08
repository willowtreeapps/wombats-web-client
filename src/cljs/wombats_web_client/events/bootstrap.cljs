(ns wombats-web-client.events.bootstrap
  (:require [ajax.core :refer [GET]]
            [cljs.core.async :as async]
            [re-frame.core :as re-frame]
            [ajax.edn :refer [edn-response-format]]
            [wombats-web-client.db :refer [default-db]]
            [wombats-web-client.socket-dispatcher :as sd]
            [wombats-web-client.events.spritesheet :refer [get-spritesheet]]
            [wombats-web-client.utils.local-storage :refer [remove-token!]]
            [wombats-web-client.utils.bootstrap :refer [token-from-url redirect-unauthenticated]]
            [wombats-web-client.constants.urls :refer [self-url]]
            [wombats-web-client.socket-dispatcher :as sd]
            [wombats-web-client.utils.auth :refer [add-auth-header]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

;; TODO: Move Helper Methods into utils

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

(defn load-user-success [current-user]
  (re-frame/dispatch-sync [:set-current-user current-user])

  ;; Connect to web socket (and polling),
  ;; fetch my wombats, and get spritesheet.
  (let [socket-ch (sd/init-ws-connection)]
    (go
      (async/<! socket-ch)
      (sd/socket-polling))))

(defn load-user-failure []
  (remove-token!)
  (redirect-unauthenticated)
  (re-frame/dispatch [:bootstrap-complete]))

(defn load-user   
  "fetches the current user" 
  []
  (GET self-url {:response-format (edn-response-format)
                 :keywords? true
                 :headers (add-auth-header {})
                 :handler load-user-success
                 :error-handler load-user-failure}))


(defn bootstrap-user []
  (load-user))

(defn bootstrap []
  ;; First check to see if token exists in db
  (let [token @(re-frame/subscribe [:auth-token])]
    (if token
      (bootstrap-user)
      (do
        (re-frame/dispatch [:bootstrap-complete])
        (redirect-unauthenticated)))))

;;   (get-spritesheet)
