(ns wombats-web-client.components.simulator.output
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]))

(defn render []
  (let [command (re-frame/subscribe [:simulator/player-command])
        player-state (re-frame/subscribe [:simulator/player-state])]
    [:div.output
     [:div.output-section
      [:h3.output-section-title "Command"]
      (prn-str @command)]

     [:div.output-section
      [:h3.output-section-title "State"]
      (prn-str @player-state)]]))
