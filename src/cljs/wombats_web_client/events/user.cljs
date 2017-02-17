(ns wombats-web-client.events.user
  (:require [re-frame.core :as re-frame]
            [ajax.core :refer [json-response-format GET PUT POST DELETE]]
            [ajax.edn :refer [edn-request-format edn-response-format]]
            [day8.re-frame.http-fx]
            [wombats-web-client.utils.auth :refer [add-auth-header get-current-user-id]]

            [wombats-web-client.db :as db]
            [wombats-web-client.utils.local-storage :refer [get-item remove-item!]]
            [wombats-web-client.constants.local-storage :refer [token]]
            [wombats-web-client.constants.urls :refer [self-url
                                                       github-signout-url
                                                       my-wombats-url
                                                       my-wombat-by-id-url]]
            [wombats-web-client.utils.socket :as ws]))

;; AUTH SPECIFIC
(defn sign-out-user
  "signs out user from server and removes their auth token"
  []
  (GET github-signout-url {:response-format :json
                           :keywords? true
                           :headers (add-auth-header {})}))

(defn sign-out-event
  []
  (re-frame/dispatch [:sign-out])
  (sign-out-user))



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

(defn delete-wombat-by-id
  "deletes wombat from db by id"
  [user-id wombat-id on-success on-error]
  (DELETE (my-wombat-by-id-url user-id wombat-id) {:response-format :json
                                                   :format (edn-request-format)
                                                   :keywoards? true
                                                   :headers (add-auth-header {})
                                                   :handler on-success
                                                   :error-handler on-error}))

(defn edit-wombat
  "edits wombat by id in db"
  [user-id wombat-id name url on-success on-error]
  (PUT (my-wombat-by-id-url user-id wombat-id) {:response-format :json
                                                :format (edn-request-format)
                                                :keywords? true
                                                :headers (add-auth-header {})
                                                :handler on-success
                                                :params {:wombat/name name :wombat/url url}
                                                :error-handler on-error}))
(defn create-new-wombat
  [name url cb-success]
  (post-new-wombat
   (get-current-user-id)
   name
   url
   (fn []
     (get-all-wombats)
     (cb-success))
   #(re-frame/dispatch [:update-modal-error (str %)])))

(defn edit-wombat-by-id
  [name url wombat-id cb-success]
  (edit-wombat
   (get-current-user-id)
   wombat-id
   name
   url
   (fn []
     (get-all-wombats)
     (cb-success))
   #(re-frame/dispatch [:update-modal-error (str %)])))

(defn delete-wombat
  [id cb-success]
  (delete-wombat-by-id
   (get-current-user-id)
   id
   (fn []
     (get-all-wombats)
     (cb-success))
   #(re-frame/dispatch [:update-modal-error (str %)])))

;; USER SPECIFIC
(defn get-current-user
  "fetches the current user object"
  [on-success on-error]
  (GET self-url {:response-format (edn-response-format)
                 :keywords? true
                 :headers (add-auth-header {})
                 :handler on-success
                 :error-handler on-error}))

(defn load-user
  "fetches the current user"
  []
  (get-current-user
   #(re-frame/dispatch [:bootstrap-user-data %]) ; success function, % = payload
   #(print "load user error")))

(re-frame/reg-event-db
 :update-user
 (fn [db [_ current-user]]
   (print current-user)
   (ws/add-user-token (:user/access-token current-user))
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

(re-frame/reg-event-db
 :user-error
 (fn [db [_ error]]
   (print "temporary error")))

(re-frame/reg-event-fx
 :bootstrap-user-data
 (fn [{:keys [db]} [_ user]]
   {:db (assoc db :auth-token (get-item token))
    :http-xhrio {:method          :get
                 :uri             (my-wombats-url (user :user/id))
                 :headers         (add-auth-header {})
                 :response-format (edn-response-format)
                 :on-success      [:update-wombats]
                 :on-failure      [:user-error]}
    :dispatch [:update-user user]
    :get-joined-games (user :user/id)}))
