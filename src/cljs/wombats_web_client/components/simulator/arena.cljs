(ns wombats-web-client.components.simulator.arena
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.components.arena :as arena]))

(defonce canvas-id "simulator-canvas")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main Method
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn render
  "Takes the simulator data object and the view mode key and renders the arena"
  [simulator-data simulator-view-mode]

  (arena/arena (simulator-view-mode @simulator-data) canvas-id)
  [:canvas {:id canvas-id}])
