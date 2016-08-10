(ns wombats_web_client.panels.home
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]))

;; home

(defn home-title []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [re-com/title
       :label (str "Hello from " @name ". This is the Wombats Home Page.")
       :level :level1])))

(defn initialize-game-action
  "Calls dispatch to initialize game"
  [id]
  (re-frame/dispatch [:initialize-game id]))



(defn game-list
  [games]
  [:div
    [:ul.game-list
    (doall (for [game games]
            ^{:key (str (:_id game) "-" (count (:players game)))}
            [:li
              (cond
                (= (:state game) "pending")
                [:div
                  [:p (str (:_id game))]
                  [:button {:on-click #(initialize-game-action (:_id game))} "Initialize Game"]
                  [:button {:on-click #(re-frame/dispatch [:delete-game (:_id game)])} "Delete Game"]]
                (= (:state game) "initialized")
                [:div
                  [:a {:href (str "#/preview/" (:_id game))} (:_id game)]
                  [:p "Click link to preview or start game"]\
                  [:button {:on-click #(re-frame/dispatch [:delete-game (:_id game)])} "Delete Game"]]
                (= (:state game) "finalized")
                [:div
                  [:p (str (:_id game) " is closed. Play featured pending.")]
                  [:button {:on-click #(re-frame/dispatch [:delete-game (:_id game)])} "Delete Game"]])]))]])

(defn create-game-button
  []
  [:input.btn {:type "button"
               :value "Create Game"
               :on-click #(re-frame/dispatch [:create-game])}])

(defn home-panel []
  (re-frame/dispatch [:fetch-games])
  (let [games (re-frame/subscribe [:games])]
    (fn []
      [:div
        [home-title]
        [create-game-button]
        [game-list @games]])))
