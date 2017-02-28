(ns wombats-web-client.utils.url
  (:require [cemerick.url :as url]
            [wombats-web-client.constants.local-storage :refer [access-token]]))

(defn strip-access-token
  "removes access token from query"
  []
  (let [url (-> js/window
                .-location
                .-href
                url/url)
        query (:query url)
        location (merge url {:query (dissoc query access-token)})
        state (or (.-state js/history) #js {})]
    (.replaceState js/history state "" location)))
