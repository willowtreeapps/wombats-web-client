(ns wombats-web-client.events.ui
  (:require [re-frame.core :as re-frame]
            [cemerick.url :as url]
            [wombats-web-client.db :refer [default-db]]
            [wombats-web-client.utils.url :refer [strip-access-token]]
            [wombats-web-client.utils.local-storage :refer [set-token! get-token]]
            [wombats-web-client.constants.local-storage :refer [access-token]]
            [wombats-web-client.events.user :refer [load-user]]))

(re-frame/reg-event-db
 :initialize-db
 (fn [db _]
   (merge default-db db)))

(re-frame/reg-event-db
 :bootstrap-app
 (fn [db [_]]
   (let [query (:query (url/url (-> js/window .-location .-href)))
        access-token-val (get query access-token)]

     ;; Access Token was pass by the server. Add token to storage,
     ;; sanitize the URL, and then load user.
     (when access-token-val
       (set-token! access-token-val)
       (strip-access-token))

     ;; Load user from localstorage
     (when (get-token)
       (load-user)))

   (assoc db :bootstrapping? true)))


(re-frame/reg-event-db
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(re-frame/reg-event-db
 :set-modal
 (fn [db [_ modal]]
   (assoc db :modal modal)))

(re-frame/reg-event-db
 :update-modal-error
 (fn [db [_ modal-error]]
   (assoc db :modal-error modal-error)))
