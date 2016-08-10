(ns wombats_web_client.panels.preview-game
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]))


;; about

(defn preview-title []
  [re-com/title
   :label "This is the Preview Page."
   :level :level1])

(defn get-game
  [game-id games]
  (first (filter (fn [game]
    (= (:_id game) game-id)) games)))

(defn join-game-action
  []
  )

(defn is-registered?
  [user players]
  (boolean (first (filter #(= (:_id user) (:_id %)) players))))

(defn register-action
  "Calls dispatch to register user in game"
  [game-id user]
  (re-frame/dispatch [:register-user-in-game game-id (:_id user) (:repo (first (:bots user)))]))

(defn preview-game-panel [meta]
  (let [games (re-frame/subscribe [:games])
        user (re-frame/subscribe [:user])
        id (:id meta)]
  (fn []
    [:div
      [preview-title]
      [:div
        "players: " (str (:players (get-game id @games)))]
      (cond (not (is-registered? @user (:players (get-game id @games))))
        [:button {:on-click #(register-action id @user)} "Join Game"])])))
