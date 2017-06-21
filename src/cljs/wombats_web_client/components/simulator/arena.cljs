(ns wombats-web-client.components.simulator.arena
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.components.arena :as arena]))

(defonce canvas-id "simulator-canvas")
(defonce dimensions 600)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main Method
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn render [frame]
  (arena/arena frame canvas-id)
  [:canvas {:id canvas-id
            :width dimensions
            :height dimensions}])
