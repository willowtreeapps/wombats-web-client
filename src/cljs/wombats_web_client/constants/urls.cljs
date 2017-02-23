(ns wombats-web-client.constants.urls)

;; Routing
(defonce panel-router-map {:view-games-panel "games"
                           :account-panel "account"
                           :config-panel "config"
                           :simulator-panel "simulator"})

;; Remote Dev
(def base-api-url "//dev.api.wombats.io")
;; Local Dev
#_(def base-api-url "//localhost:8888")

(def self-url (str base-api-url "/api/v1/self"))
(def github-signout-url (str base-api-url "/api/v1/auth/github/signout"))
(def github-signin-url (str base-api-url "/api/v1/auth/github/signin"))

(defonce spritesheet-url "/spritesheet.json")

(def ws-url (str (case js/window.location.protocol
                   "https:" "wss:"
                   "ws:")
                 base-api-url
                 "/ws/game"))

(defn my-wombats-url
  [id]
  (str base-api-url "/api/v1/users/" id "/wombats"))

(defn my-wombat-by-id-url
  [user-id id]
  (str base-api-url "/api/v1/users/" user-id "/wombats/" id))

(def games-url (str base-api-url "/api/v1/games"))

(defn games-join-url [game-id]
  (str base-api-url "/api/v1/games/" game-id "/join"))
