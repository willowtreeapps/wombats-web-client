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

(defn get-game-state-str [is-full is-playing]
  (cond
   is-full "FULL"
   is-playing "ACTIVE"
   :else nil))
