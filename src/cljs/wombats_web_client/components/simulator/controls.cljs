(ns wombats-web-client.components.simulator.controls
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.utils.socket :as ws]))

(defn- on-step-click!
  [evt sim-state]
  (ws/send-message :process-simulation-frame {:game-state @sim-state}))

(defn render
  [sim-state]
  [:button {:on-click #(on-step-click! % sim-state)} "Step"])
