(ns wombats-web-client.utils.bootstrap
  (:require [pushy.core :as pushy]
            [re-frame.core :as re-frame]
            [wombats-web-client.routes :refer [history]]
            [wombats-web-client.utils.local-storage :refer [remove-token!]]))

(defn redirect-authenticated []
  (pushy/replace-token! history "/"))

(defn redirect-unauthenticated []
  (pushy/replace-token! history "/welcome"))

(defn bootstrap-failure [error]
  (js/console.error error)
  (remove-token!)
  (redirect-unauthenticated)
  (re-frame/dispatch [:bootstrap-complete]))
