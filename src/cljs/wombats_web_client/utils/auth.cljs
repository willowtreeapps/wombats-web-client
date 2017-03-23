(ns wombats-web-client.utils.auth
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.utils.local-storage :refer [get-token]]))

(defn get-auth-header
  "returns an Authorization header if one should be present"
  []
  {:Authorization (get-token)})

(defn add-auth-header
  "Add token to header"
  [headers]
  (merge (get-auth-header) headers))

(defn get-current-user-id []
  (:user/id @(re-frame/subscribe [:current-user])))

(defn is-coordinator? [user]
  (let [roles (:user/roles user)]
    (reduce (fn [is-admin role]
              (let [role-name (:db/ident role)]
                (or
                 is-admin
                 (= role-name :user.roles/admin)
                 (= role-name :user.roles/coordinator)))) false roles)))

(defn user-is-coordinator? []
  (is-coordinator? @(re-frame/subscribe [:current-user])))