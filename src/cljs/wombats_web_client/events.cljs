(ns wombats-web-client.events
    (:require [re-frame.core :as re-frame]
              [cemerick.url :as url]

              [wombats-web-client.events.ui]
              [wombats-web-client.events.user]

              [wombats-web-client.db :as db]
              [wombats-web-client.utils.url :refer [strip-access-token]]
              [wombats-web-client.utils.local-storage :refer [set-item! get-item]]
              [wombats-web-client.services.user :refer [get-current-user]]
              [wombats-web-client.constants.local-storage :refer [access-token token]]))

(defn load-user
  "fetches the current user"
  []
  (get-current-user
   #(re-frame/dispatch [:update-user %]) ; success function, % = payload
   (print "error")))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/reg-event-db 
 :bootstrap-app
 (fn [db [_]]
   (let [query (:query (url/url (-> js/window .-location .-href)))
        access-token-val (get query access-token)]

     ;; Access Token was pass by the server. Add token to storage,
     ;; sanitize the URL, and then load user.
     (when access-token
       (set-item! token access-token-val)
       (strip-access-token))

     ;; Load user from localstorage
     (when (get-item token)
          (load-user)))

   (assoc db :bootstrapping? true)))
