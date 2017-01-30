(ns wombats-web-client.events.user
  (:require [re-frame.core :as re-frame]
            
            [wombats-web-client.db :as db]
            [wombats-web-client.utils.local-storage :refer [remove-item!]]
            [wombats-web-client.services.user :refer [sign-out-user 
                                                      post-new-wombat 
                                                      load-wombats]]
            [wombats-web-client.constants.local-storage :refer [token]]))

;; HELPERS
(defn get-current-user-id []
  (let [current-user (re-frame/subscribe [:current-user])]
    (@current-user :id)))

;; AUTH SPECIFIC
(defn sign-out
  []
  (sign-out-user
   #(re-frame/dispatch [:sign-out %])
   (fn [] (print "error with sign-out"))))

;; WOMBAT SPECIFIC
(defn get-all-wombats []
  (load-wombats
   (get-current-user-id)
   #(re-frame/dispatch [:update-wombats %])
   (fn [] (print "error with get-all-wombats"))))

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
(re-frame/reg-event-db
 :set-modal
 (fn [db [_ modal-form]]
   (assoc db :modal modal-form)))

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
