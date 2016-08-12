(ns wombats_web_client.panels.preview-game
  (:require [re-frame.core :as re-frame]
            [wombats_web_client.services.forms :as f]
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

(defn start-game-action
  "Calls dispatch to finalize game. No other players can join afterwards."
  [game-id]
  (re-frame/dispatch [:start-game game-id]))

(defn is-valid?
  [form]
  (boolean (f/get-value :repo form)))

(defn register-user
  [game-id user]
  (let [form (f/initialize {:repo nil})
        bots (:bots @user)
        _id (:_id @user)]
    [:div
     (for [bot bots]
       ^{:key (:repo bot)} [:div
                            [:input {:type "radio"
                                     :name "bot"
                                     :value (:repo bot)
                                     :on-change #(f/set-value! :repo (-> % .-target .-value) form)}]
                            [:p (:name bot)]])
     [:input {:type "button"
              :value "Join Game"
              :on-click #(f/on-submit {:form form
                                       :validator is-valid?
                                       :dispatch [:register-user-in-game
                                                  game-id
                                                  _id
                                                  (get-in @form [:doc :repo])]})}]]))

(defn player-modal
  [players]
  [:div
   [:ul.player-list
    (for [player players]
      ^{:key (:_id player)} [:li.player (:login player)])]])

(defn preview-game-panel [meta]
  (re-frame/dispatch [:fetch-games])
  (let [games (re-frame/subscribe [:games])
        user (re-frame/subscribe [:user])
        id (:id meta)]
  (fn []
    [:div
      [preview-title]
      [:div
        [:button {:on-click #(re-frame/dispatch [:display-modal (player-modal (:players (get-game id @games)))])} "View Players"]
      (cond
        (not (is-registered? @user (:players (get-game id @games))))
        [:button {:on-click #(re-frame/dispatch [:display-modal (register-user id user)])} "Join Game"]

        (and (is-registered? @user (:players (get-game id @games))) (= (:state (get-game id @games)) "initialized"))
        [:button {:on-click #(start-game-action (:_id (get-game id @games)))} "Start Game"])]])))
