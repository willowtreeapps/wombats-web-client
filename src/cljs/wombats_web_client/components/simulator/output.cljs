(ns wombats-web-client.components.simulator.output
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]))

(defn- format-code
  [clj-object]
  [:pre (.stringify js/JSON (clj->js clj-object) nil 2)])

(defn render []
  (let [command (re-frame/subscribe [:simulator/player-command])
        player-state (re-frame/subscribe [:simulator/player-state])]
    [:div.output
     [:div.output-section
      [:h3.output-section-title "Command"]
      (format-code @command)]

     [:div.output-section
      [:h3.output-section-title "State"]
      (format-code @player-state)]]))
