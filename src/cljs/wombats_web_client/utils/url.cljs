(ns wombats-web-client.utils.url
  (:require [pushy.core :as pushy]
            [wombats-web-client.routes :refer [history]]))

(defn strip-access-token
  "removes access token from query"
  []
  (pushy/replace-token! history "/"))
