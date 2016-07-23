(ns wombats_web_client.handlers.root
    (:require [re-frame.core :as re-frame]
              [cemerick.url :as url]

              [wombats_web_client.db :as db]

              ;; Handlers
              [wombats_web_client.handlers.ui]
              [wombats_web_client.handlers.users]
              [wombats_web_client.handlers.account]

              [wombats_web_client.services.wombats :refer [get-current-user]]
              [wombats_web_client.services.utils :refer [set-item! get-item]]))

(defn- strip-access-token
  "removes access token from query"
  []
  (let [url (url/url (-> js/window .-location .-href))
        query (:query url)
        location (str (merge url {:query (dissoc query "access-token")}))]
    (set! (.-location js/window) location)
    (js/console.log "b =>" (pr-str location))))

(defn- load-user
  "fetches the current user"
  []
  (get-current-user
   #(re-frame/dispatch [:update-user %]) ; success function, % = payload
   #(re-frame/dispatch [:update-errors %])))

(defn- initialize-db
  "initializes application state on bootstrap"
  [_ _]
  db/default-db)

(defn- bootstrap
  "makes all necessary requests to initially bootstrap an application"
  [db _]
  (let [query (:query (url/url (-> js/window .-location .-href)))
        access-token (get query "access-token")]

    ;; Access Token was pass by the server. Add token to storage,
    ;; sanitize the URL, and then load user.
    (when access-token
      (set-item! "token" access-token)
      (strip-access-token))

    ;; Load user from localstorage
    (when (get-item "token")
      (load-user)))

  (assoc db :bootstrapping? true))

(re-frame/register-handler :initialize-db initialize-db)
(re-frame/register-handler :bootstrap-app bootstrap)
