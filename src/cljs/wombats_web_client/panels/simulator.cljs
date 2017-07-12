(ns wombats-web-client.panels.simulator
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.components.arena
             :as arena]
            [wombats-web-client.components.tabbed-container
             :refer [tabbed-container]]
            [wombats-web-client.components.simulator.arena
             :as simulator-arena]
            [wombats-web-client.components.simulator.code
             :as simulator-code]
            [wombats-web-client.components.simulator.output
             :as simulator-output]
            [wombats-web-client.components.simulator.stack-trace
             :as simulator-stack-trace]
            [wombats-web-client.components.simulator.configure
             :refer [configuration-panel]]
            [wombats-web-client.components.simulator.controls
             :as simulator-controls]
            [wombats-web-client.events.simulator
             :refer [get-simulator-templates]]
            [wombats-web-client.components.simulator.split-pane
             :as split-pane]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Lifecycle Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- component-will-mount! []
  (get-simulator-templates))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Render Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def update-sim (reagent/atom false))
(def pane-label (reagent/atom "Debug Console"))
(defn- render-right-pane
  [simulator-data]
  (let [bottom-pane (if (:player-stack-trace @simulator-data)
                      (do (reset! pane-label "Stack Trace")
                          [simulator-stack-trace/render simulator-data])
                      (do (reset! pane-label "Debug Console")
                          [simulator-output/render simulator-data update-sim]))]
    [:div {:class-name "right-pane"}
     [split-pane/render [simulator-code/render {:simulator-data  simulator-data
                                                :update update-sim}]
      bottom-pane
      update-sim pane-label]]))

(defn- render-left-pane
  [{:keys
    [simulator-view-mode simulator-data simulator-frames simulator-index]}]
  [:div.left-pane
   [simulator-arena/render simulator-data simulator-view-mode]
   [simulator-controls/render simulator-data simulator-frames simulator-index]])

(defn- render
  [{:keys [simulator-view-mode
           simulator-frames
           simulator-index
           simulator-data]}]
  (if (neg? @simulator-index) ;; fix reset to config screen bug
    [configuration-panel]
    [:div {:class-name "simulator-panel"}
     [render-left-pane {:simulator-view-mode simulator-view-mode
                        :simulator-data simulator-data
                        :simulator-frames simulator-frames
                        :simulator-index simulator-index}]
     [render-right-pane simulator-data]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main Method
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn simulator []
  (reagent/create-class
   {:component-will-mount #(component-will-mount!)
    :props-name "simulator-panel"
    :reagent-render
    #(render {:simulator-view-mode @(re-frame/subscribe
                                     [:simulator/get-view-mode])
              :simulator-frames (re-frame/subscribe
                                  [:simulator/frames])
              :simulator-data (re-frame/subscribe
                                [:simulator/get-data])
              :simulator-index (re-frame/subscribe
                                 [:simulator/frame-index])})}))
