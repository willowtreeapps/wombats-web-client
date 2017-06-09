(ns wombats-web-client.constants.urls)

;; Routing
(defonce panel-router-map {:view-games-panel "games"
                           :account-panel "account"
                           :config-panel "config"
                           :simulator-panel "simulator"})

(goog-define base-api-url "//api.wombats.io")
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

(defonce arenas-url (str base-api-versioned-url "/arenas"))
(defonce games-url (str base-api-versioned-url "/games"))
(defonce access-key-url (str base-api-versioned-url "/access_keys"))

(defn games-join-url [game-id]
  (str base-api-versioned-url "/games/" game-id "/join"))

(defn create-game-url [arena-id]
  (str base-api-versioned-url "/games?arena-id=" arena-id))

(defonce simulator-templates-url
  (str base-api-versioned-url "/simulator/templates"))

(defonce initialize-simulator-url
  (str base-api-versioned-url "/simulator/initialize"))

(defonce process-simulator-frame-url
  (str base-api-versioned-url "/simulator/process_frame"))
