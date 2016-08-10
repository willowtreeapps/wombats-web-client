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

(defn is-registered?
  [user players]
  (boolean (first (filter #(= (:_id user) (:_id %)) players))))

(defn register-user-action
  "Calls dispatch to register user in game"
  [game-id user]
  (re-frame/dispatch [:register-user-in-game game-id (:_id user) (:repo (first (:bots user)))]))
;
; (defn start-game-action
;   "Calls dispatch to finalize game. No other players can join afterwards."
;   [game-id]
;   (re-frame/dispatch [:start-game game-id]))

(defn preview-game-panel [meta]
  (let [games (re-frame/subscribe [:games])
        user (re-frame/subscribe [:user])
        id (:id meta)]
  (fn []
    [:div
      [preview-title]
      [:div
        "players: " (str (:players (get-game id @games)))]
      (cond
        (not (is-registered? @user (:players (get-game id @games))))
        [:button {:on-click #(register-user-action id @user)} "Join Game"]
        ; (is-registered? @user (:players (get-game id @games)))
        ; [:button {:on-click #(start-game-action (:_id (get-game id @games)))} "Start Game"]
        )])))
