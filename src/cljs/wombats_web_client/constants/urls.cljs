(ns wombats-web-client.constants.urls)

(def base-api-url "//dev.wombats.io")

(def self-url (str base-api-url "/api/v1/self"))
(def github-signout-url (str base-api-url "/api/v1/auth/github/signout"))
(def github-signin-url (str base-api-url "/api/v1/auth/github/signin"))

(def game-url (str "ws:" base-api-url "/ws/game"))

(defn my-wombats-url
  [id]
  (str base-api-url "/api/v1/users/" id "/wombats"))
<<<<<<< Updated upstream
=======

(defn my-wombat-by-id-url
  [user-id id]
  (str base-api-url "/api/v1/users/" user-id "/wombats/" id))

(def games-url (str base-api-url "/api/v1/games"))

(defn games-join-url [game-id] 
  (str base-api-url "/api/v1/games/" game-id "/join"))
>>>>>>> Stashed changes
