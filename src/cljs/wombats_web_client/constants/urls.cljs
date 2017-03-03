(ns wombats-web-client.constants.urls)

;; Routing
(defonce panel-router-map {:view-games-panel "games"
                           :account-panel "account"
                           :config-panel "config"
                           :simulator-panel "simulator"})

;; Remote Dev
(defonce base-api-url "//dev.api.wombats.io")

;; Local Dev
#_(defonce base-api-url "//localhost:8888")

(defonce base-api-versioned-url (str base-api-url "/api/v1"))

(defonce self-url (str base-api-versioned-url "/self"))
(defonce github-signout-url (str base-api-versioned-url "/auth/github/signout"))
(defonce github-signin-url (str base-api-versioned-url "/auth/github/signin"))

(defonce spritesheet-url "/spritesheet.json")

(defonce ws-url (str (case js/window.location.protocol
                   "https:" "wss:"
                   "ws:")
                 base-api-url
                 "/ws/game"))

(defn my-wombats-url
  [id]
  (str base-api-versioned-url "/users/" id "/wombats"))

(defn my-wombat-by-id-url
  [user-id id]
  (str base-api-versioned-url "/users/" user-id "/wombats/" id))

(defonce games-url (str base-api-versioned-url "/games"))

(defn games-join-url [game-id]
  (str base-api-versioned-url "/games/" game-id "/join"))

(defonce simulator-templates-url (str base-api-versioned-url "/simulator/templates"))
