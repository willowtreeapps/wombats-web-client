(ns wombats-web-client.utils.errors)

(defonce required-field-error
  "This field is required.")
(defonce wombat-color-missing
  "Please select a color for your wombat.")
(defonce game-full-error
  "This game is already full. Please try joining another game.")
(defonce game-started-error
  "This game has already started. Please try joining another game.")
(defonce not-an-integer
  "Not an integer.")
(defonce min-five "Minimum is 5.")
(defonce max-eight "Max is 8.")
(defonce max-twenty-five "Max is 25.")
(defonce incorrect-format-colon "Format is mm:ss.")

(def login-error "login-error")

(defn get-error-code [error]
  (:code (:response error)))

(defn get-error-message [error]
  (:message (:response error)))

(defn get-field-error-keyword [error]
  (:field-error (:response error)))

(defn has-field-error? [error field]
  (= field (get-field-error-keyword error)))

(defn is-game-full? [error]
  (= (get-error-code error) :game-full))

(defn has-game-started? [error]
  (let [code (get-error-code error)]
    (or (= code :unable-to-join-active)
        (= code :unable-to-join-active-intermission))))
