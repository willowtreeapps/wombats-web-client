(ns wombats-web-client.constants.urls)

(def base-api-url "//54.145.152.66")

(def self-url (str base-api-url "/api/v1/self"))
(def github-signout-url (str base-api-url "/api/v1/auth/github/signout"))
(def github-signin-url (str base-api-url "/api/v1/auth/github/signin"))

(def game-url (str "ws:" base-api-url "/ws/game"))