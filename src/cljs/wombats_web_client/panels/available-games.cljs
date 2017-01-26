(ns wombats-web-client.panels.available-games
  (:require [re-frame.core :as re-frame]))

;; Available Games Panel

(defn available-games
  []
  (print "render availablegames")
  (fn []
    [:div (str "This is the Available Games page.")]))
