(ns wombats-web-client.events.user
  (:require [re-frame.core :as re-frame]
            
            [wombats-web-client.db :as db]
            [wombats-web-client.utils.local-storage :refer [remove-item!]]
            [wombats-web-client.services.user :refer [sign-out-user]]
            [wombats-web-client.constants.local-storage :refer [token]]))

;; AUTH SPECIFIC
(defn sign-out
  []
  (sign-out-user
   #(re-frame/dispatch [:sign-out %])
   (print "error")))


;; USER SPECIFIC
(re-frame/reg-event-db
 :update-user
 (fn [db [_ current-user]]
   (assoc db :current-user current-user)))

(re-frame/reg-event-db
  :sign-out
  (fn [db [_ _]]
    (remove-item! token)
    (assoc db :auth-token nil :current-user nil)))

