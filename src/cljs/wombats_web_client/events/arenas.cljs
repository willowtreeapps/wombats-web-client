(ns wombats-web-client.events.arenas
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.constants.urls :refer [arenas-url]]
            [ajax.edn :refer [edn-request-format edn-response-format]]
            [ajax.core :refer [GET POST]]
            [wombats-web-client.utils.auth :refer [add-auth-header]]))

(defn create-arena
  [params]
  (let [on-success (:on-success params)
        on-error (:on-error params)
        arena-params (dissoc params :on-success :on-error)]

    (println arena-params)
    (println on-success)
    (POST arenas-url {:response-format (edn-response-format)
                      :keywords? true
                      :format (edn-request-format)
                      :headers (add-auth-header {})
                      :params arena-params
                      :handler on-success
                      :error-handler on-error})))

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
