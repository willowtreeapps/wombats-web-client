(ns wombats-web-client.panels.open-games
  (:require [re-frame.core :as re-frame]))

;; Open Games Panel

(defn open-games
  []
  (fn []
    [:div (str "This is the Open Games page.")]))
