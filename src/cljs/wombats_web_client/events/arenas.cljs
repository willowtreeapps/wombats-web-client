(ns wombats-web-client.events.arenas
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.constants.urls :refer [arenas-url
                                                       arena-id-url]]
            [ajax.edn :refer [edn-request-format edn-response-format]]
            [ajax.core :refer [GET POST PUT DELETE]]
            [wombats-web-client.utils.auth :refer [add-auth-header]]))

(defn create-arena
  [{:keys [on-success
           on-error] :as all}]
  (let [params (dissoc all :on-success :on-error)]
    (POST arenas-url {:response-format (edn-response-format)
                      :keywords? true
                      :format (edn-request-format)
                      :headers (add-auth-header {})
                      :params params
                      :handler on-success
                      :error-handler on-error})))

(defn edit-arena
  [{:keys [arena/id
           on-success
           on-error] :as all}]
  (let [params (dissoc all :on-success :on-error)]
    (PUT (arena-id-url id) {:response-format (edn-response-format)
                           :keywords? true
                           :format (edn-request-format)
                           :headers (add-auth-header {})
                           :params params
                           :handler on-success
                           :error-handler on-error})))

(defn delete-arena
  "Delete an arena by id"
  [arena-id on-success on-error]
  (DELETE (arena-id-url arena-id)
          {:response-format (edn-response-format)
           :keywords? true
           :format (edn-request-format)
           :headers (add-auth-header {})
           :handler on-success
           :error-handler on-error}))

(defn get-arenas []
  (GET arenas-url {:response-format (edn-response-format)
                  :keywords? true
                  :format (edn-request-format)
                  :headers (add-auth-header {})
                  :handler #(re-frame/dispatch [:update-arenas %])
                  :error-handler #(print "error on get-arenas")}))

(re-frame/reg-event-db
 :update-arenas
 (fn [db [_ arenas]]
   (assoc db :arenas arenas)))
