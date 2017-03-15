(ns wombats-web-client.utils.games)

(defn build-status-query [statuses]
  (clojure.string/join "&status=" statuses))

(defn get-occupied-colors [game]
  (let [players (:game/players game)]
    (reduce (fn [coll player]
              (conj coll (:player/color player)))
            [] players)))

(defn get-user-in-game [players current-user]
  (let [current-username (:user/github-username current-user)]
    (filter (fn [player]
              (let [user (:player/user player)
                    github-username (:user/github-username user)]
                (= github-username current-username))) players)))

(defn- user-in-game?
  "Whether current-user is in the game"
  [current-user game]
  (pos?
   (count 
    (filter 
     #(= (:user/github-username current-user)
         (get-in % [:player/user :user/github-username]))
       (:game/players game)))))

(defn get-game-state-str [is-full is-playing]
  (cond
   is-full "FULL"
   is-playing "ACTIVE"
   :else nil))

(defn get-player
  [db]
  "Pulls the player out of db to use for simulator state"
  (let [players (get-in db [:simulator/state :players])
        player-key (first (keys players))]
    (get players player-key)))

(defn- is-open?
  "Whether the game is in an open state"
  [game]
  (let [status (:game/status game)]
    (or (= status :pending-open)
        (= status :pending-closed)
        (= status :active)
        (= status :active-intermission))))

(defn- is-closed?
  "Whether the game is in a closed state"
  [game]
  (let [status (:game/status game)]
    (= status :closed)))

(defn get-open-games
  [games]
  (reduce-kv (fn [coll _ game]
               (if (is-open? game)
                 (conj coll game)
                 coll)) [] games))

(defn get-my-open-games
  [games current-user]
  (reduce-kv (fn [coll _ game]
               (if (and (is-open? game)
                        (user-in-game? current-user game))
                 (conj coll game)
                 coll)) [] games))

(defn get-closed-games
  [games]
  (reduce-kv (fn [coll _ game]
               (if (is-closed? game)
                 (conj coll game)
                 coll)) [] games))

(defn get-my-closed-games
  [games current-user]
  (reduce-kv (fn [coll _ game]
               (if (and (is-closed? game)
                        (user-in-game? current-user game))
                 (conj coll game)
                 coll)) [] games))
