(ns wombats-web-client.constants.urls)

(def base-ws-url "//54.145.152.66")
(def base-api-url "http://dev.wombats.io")

(def self-url (str base-api-url "/api/v1/self"))
(def github-signout-url (str base-api-url "/api/v1/auth/github/signout"))
(def github-signin-url (str base-api-url "/api/v1/auth/github/signin"))

(def game-url (str "ws:" base-ws-url "/ws/game"))

(defn my-wombats-url
  [id]
  (str base-api-url "/api/v1/users/" id "/wombats"))
