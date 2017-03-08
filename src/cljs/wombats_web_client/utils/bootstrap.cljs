(ns wombats-web-client.utils.bootstrap
  (:require [cemerick.url :as url]
            [pushy.core :as pushy]
            [re-frame.core :as re-frame]
            [wombats-web-client.routes :refer [history]]
            [wombats-web-client.constants.local-storage :refer [access-token]]
            [wombats-web-client.utils.local-storage :refer [remove-token! set-token!]]
            [wombats-web-client.utils.url :refer [strip-access-token]]))

(defn token-from-url []
  (let [query (:query (url/url (-> js/window .-location .-href)))
        access-token-val (get query access-token)]

    ;; Access Token was pass by the server. Add token to storage,
    ;; sanitize the URL, and then load user.
    (when access-token-val
      (set-token! access-token-val)
      (strip-access-token))

    access-token-val))

(defn redirect-unauthenticated []
  (pushy/replace-token! history "/welcome"))

(defn bootstrap-failure [error]
  (js/console.error error)
  (remove-token!)
  (redirect-unauthenticated)
  (re-frame/dispatch [:bootstrap-complete]))