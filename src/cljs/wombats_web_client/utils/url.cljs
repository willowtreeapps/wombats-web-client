(ns wombats-web-client.utils.url
  (:require [cemerick.url :as url]
            [wombats-web-client.constants.local-storage :refer [access-token]]))

(defn strip-access-token
  "removes access token from query"
  []
  ;; Love the threading, you could add url/url to it as well
  ;; And maybe even pull it out into its own fn
  (comment
    (let [url (-> js/window
                  .-location
                  .-href
                  url/url)]))

  (let [url (url/url (-> js/window .-location .-href))
        query (:query url)
        location (str (merge url {:query (dissoc query access-token)}) "#/")
        state (or (.-state js/history) #js {})]
    (.replaceState js/history state "" location)))
