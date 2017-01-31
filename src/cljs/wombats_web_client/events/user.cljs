(ns wombats-web-client.events.user
  (:require [re-frame.core :as re-frame]
            [ajax.core :refer [GET POST]]
            [ajax.edn :refer [edn-request-format]]
            [wombats-web-client.utils.auth :refer [add-auth-header]]

            [wombats-web-client.db :as db]
            [wombats-web-client.utils.local-storage :refer [remove-item!]]
            [wombats-web-client.constants.local-storage :refer [token]]
            [wombats-web-client.constants.urls :refer [self-url 
                                                       github-signout-url 
                                                       my-wombats-url]]))

;; HELPERS
(defn get-current-user-id []
  (let [current-user (re-frame/subscribe [:current-user])]
    (@current-user :id)))



;; AUTH SPECIFIC
(defn sign-out-user
  "signs out user from server and removes their auth token"
  [on-success on-error]
  (GET github-signout-url {:response-format :json
                           :keywords? true
                           :headers (add-auth-header {})
                           :handler on-success
                           :error-handler on-error}))

(defn sign-out
  []
  (sign-out-user
   #(re-frame/dispatch [:sign-out %])
   (fn [] (print "error with sign-out"))))



;; USER WOMBAT SPECIFIC
(defn load-wombats
  "loads all wombats associated with user id"
  [id on-success on-error]
  (GET (my-wombats-url id) {:response-format :json
                              :keywords? true
                              :headers (add-auth-header {})
                              :handler on-success
                              :error-handler on-error}))

(defn get-all-wombats []
  (load-wombats
   (get-current-user-id)
   #(re-frame/dispatch [:update-wombats %])
   (fn [] (print "error with get-all-wombats"))))

(defn post-new-wombat
  "creates and returns a wombat"
  [id name url on-success on-error]
  (POST (my-wombats-url id) {:response-format :json
                                  :format (edn-request-format)
                                :keywords? true
                                :headers (add-auth-header {})
                                :handler on-success
                                :params {:wombat/name name :wombat/url url}
                                :error-handler on-error}))

(defn create-new-wombat
  [name url cb-success cb-error]
  (post-new-wombat
   (get-current-user-id)
   name
   url
   (fn [] 
     (get-all-wombats)
     (cb-success))
   (fn [] 
     (print "error with create-new-wombat")
     (cb-error))))

;; USER SPECIFIC
(defn get-current-user
  "fetches the current user object"
  [on-success on-error]
  (GET self-url {:response-format :json
                 :keywords? true
                 :headers (add-auth-header {})
                 :handler on-success
                 :error-handler on-error}))

(defn load-user
  "fetches the current user"
  []
  (get-current-user
   #(re-frame/dispatch [:update-user %]) ; success function, % = payload
   (print "error")))

(re-frame/reg-event-db
 :update-user
 (fn [db [_ current-user]]
   (assoc db :current-user current-user)))

(re-frame/reg-event-db
  :sign-out
  (fn [db [_ _]]
    (remove-item! token)
    (assoc db :auth-token nil :current-user nil)))

(re-frame/reg-event-db
 :update-wombats
 (fn [db [_ wombats]]
   (assoc db :my-wombats wombats)))
