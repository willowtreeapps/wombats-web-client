(ns wombats-web-client.components.simulator.arena
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.components.arena :as arena]))

(defonce canvas-id "simulator-canvas")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main Method
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn render [simulator-frames simulator-index simulator-view-mode]

  (arena/arena-history
   {:frames-vec  simulator-frames
    :frames-idx  simulator-index
    :view-mode simulator-view-mode
    :canvas-id canvas-id})
  [:canvas {:id canvas-id
            :width dimensions
            :height dimensions}])
