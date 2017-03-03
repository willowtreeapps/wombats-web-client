(ns wombats-web-client.components.simulator.output
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]))

(defn render!
  [command player-state]

  [:div.output
   [:div.output-section
    [:h3.output-section-title "Command"]
    (prn-str command)]

   [:div.output-section
    [:h3.output-section-title "State"]
    (prn-str player-state)]])

(defn render
  []
  (let [command (re-frame/subscribe [:simulator/player-command])
        player-state (re-frame/subscribe [:simulator/player-state])]
    (render! @command @player-state)))
