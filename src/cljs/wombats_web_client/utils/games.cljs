(ns wombats-web-client.utils.games)

(defn build-status-query [statuses page]
  (str (clojure.string/join "&status=" statuses)
       "&page=" page))

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
  (let [players (get-in db [:simulator/state :game/players])
        player-key (first (keys players))]
    (get players player-key)))

(defn get-player-frames-vec
  "Given state pull out the first player component"
  [state]
  (let [players (:game/players state)
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

(defn is-private?
  "Whether the game is private"
  [game-status]
  (= "private" game-status))

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

(defn get-player-by-username
  [username players]
  (first
   (filter #(= (get-in %
                       [:player/user
                        :user/github-username])
               username)
           players)))

(defn get-player-score
  [player]
  (get-in player [:player/stats :stats/score]))

(defn sort-players
  [players-map]
  (sort #(compare (get-player-score %2)
                  (get-player-score %1))
        (vals players-map)))

(defn get-arena-from-game
  [game]
  (get-in game [:game/frame :frame/arena]))

(defn get-wombats-in-arena
  [arena]
  (let [flattened-arena (flatten arena)]
    (filter #(= (get-in % [:contents :type])
                :wombat)
            flattened-arena)))

(defn filter-wombats-by-color
  [wombats color]
  (first ;; There can only be one wombat per color
   (filter #(= (get-in % [:contents :color])
               color)
           wombats)))

(defn get-wombat-in-game
  [game color]
  (let [arena (get-arena-from-game game)
        wombats (get-wombats-in-arena arena)]
    (filter-wombats-by-color wombats color)))
