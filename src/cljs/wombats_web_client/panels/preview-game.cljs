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

(defn preview-game-panel [meta]
  (let [games (re-frame/subscribe [:games])]
  (fn []
    [:div
      [preview-title]
      (str (get-game (:id meta) @games))])))
