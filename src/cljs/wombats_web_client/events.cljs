(ns wombats-web-client.events
    (:require [re-frame.core :as re-frame]
              [cemerick.url :as url]

              [wombats-web-client.db :as db]
              [wombats-web-client.handlers :refer [get-current-user, sign-out-user]]))

;;
;; Local Storage
;;
;; https://gist.github.com/daveliepmann/cf923140702c8b1de301
(defn set-item!
  "Set `key' in browser's localStorage to `val`."
  [key val]
  (.setItem (.-localStorage js/window) key val))

(defn get-item
  "Returns value of `key' from browser's localStorage."
  [key]
  (.getItem (.-localStorage js/window) key))

(defn remove-item!
  "Remove the browser's localStorage value for the given `key`"
  [key]
  (.removeItem (.-localStorage js/window) key))

(defn strip-access-token
  "reoves access token from query"
  []
  (let [url (url/url (-> js/window .-location .-href))
        query (:query url)
        location (str (merge url {:query (dissoc query "access-token")}) "#/")
        state (or (.-state js/history) #js {})]
    (.replaceState js/history state "" (str location)))

  (defn load-user
  "fetches the current user"
  []
  (get-current-user
   #(re-frame/dispatch [:update-user %]) ; success function, % = payload
   (print "error"))))

(defn sign-out
  []
  (sign-out-user
   #(re-frame/dispatch [:sign-out %])
   (print "error")))

(defn server-sign-out
  "sign out of server"
  []
  sign-out-user
  #(re-frame/dispatch [:sign-out])
  (print "error"))


(re-frame/reg-event-db
  :sign-out
  (fn [db [_ _]]
    (print "signing out locally")
    (remove-item! "token")
    (assoc db :auth-token nil :current-user nil)))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/reg-event-db
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(re-frame/reg-event-db 
 :bootstrap-app
 (fn [db [_]]
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

   (assoc db :bootstrapping? true)))


(re-frame/reg-event-db
 :update-user
 (fn [db [_ current-user]]
   (print "event handling " current-user)
   (assoc db :current-user current-user)))
