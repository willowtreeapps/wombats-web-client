(ns wombats_web_client.handlers.root
    (:require [re-frame.core :as re-frame]
              [wombats_web_client.db :as db]

              ;; Handlers
              [wombats_web_client.handlers.ui]
              [wombats_web_client.handlers.users]
              [wombats_web_client.handlers.account]
              [cemerick.url :as url]

              [wombats_web_client.services.wombats :refer [get-current-user]]
              [wombats_web_client.services.utils :refer [set-item! get-item]]))

(defn initialize-app-state
  "initializes application state on bootstrap"
  [_ _]
  db/default-db)

(defn strip-access-token
  "removes access token from query"
  []
  (let [url (url/url (-> js/window .-location .-href))
        query (:query url)
        location (str (merge url {:query (dissoc query "access-token")}))]
    (set! (.-location js/window) location)
    (js/console.log "b =>" (pr-str location))))

(defn load-user
  "fetches the current user"
  []
  (print "loading user")
  (get-current-user
   #(re-frame/dispatch [:update-user-client %]) ; success function, % = payload
   #(re-frame/dispatch [:update-errors %])))

; (defn initialize-socket-connection
;   [db _]
;   (let [{:keys [chsk
;                 ch-recv
;                 send-fn
;                 state]} (sente/make-channel-socket! "/chsk" {:type :auto
;                                                                    :packer :edn
;                                                                    :params {:access-token (get-item "token")}
;                                                                    :wrap-recv-envs false})
;       sente-connection {:chsk chsk
;                         :ch-chsk ch-recv
;                         :chsk-send! send-fn
;                         :chsk-state state}]
;   (initialize-sente-router sente-connection)
;   (assoc db :socket-connection sente-connection)))

(defn bootstrap
  "makes all necessary requests to initially bootstrap an application"
  [db _]
  (let [query (:query (url/url (-> js/window .-location .-href)))
        access-token (get query "access-token")]
    (if access-token
      ;; Access Token was pass by the server. Add token to storage,
      ;; sanitize the URL, and then load user.
      (do
        (set-item! "token" access-token)
        (strip-access-token))
      ;; User has a token in storage. Load user.
      (if (get-item "token")
        (do
          (load-user)))))
        ;; (re-frame/dispatch [:initialize-socket-connection])))))

  (assoc db :bootstrapping? true))

; (re-frame/register-handler :initialize-app initialize-app-state)
(re-frame/register-handler :bootstrap-app bootstrap)
; (re-frame/register-handler :initialize-socket-connection initialize-socket-connection)
