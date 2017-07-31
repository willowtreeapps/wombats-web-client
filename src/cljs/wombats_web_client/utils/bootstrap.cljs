(ns wombats-web-client.utils.bootstrap
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.routes :refer [nav!]]
            [wombats-web-client.utils.local-storage :refer [remove-token!
                                                            set-token!]]))

(defn redirect-authenticated [token]
  (set-token! token)
  (nav! "/"))

(defn redirect-unauthenticated []
  (nav! "/welcome"))

(defn bootstrap-failure [error]
  (js/console.error error)
  (remove-token!)
  (redirect-unauthenticated)
  (re-frame/dispatch [:bootstrap-complete]))
