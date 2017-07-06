(ns wombats-web-client.panels.simulator
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.components.arena :as arena]
            [wombats-web-client.components.tabbed-container
             :refer [tabbed-container]]
            [wombats-web-client.components.simulator.arena :as simulator-arena]
            [wombats-web-client.components.simulator.code :as simulator-code]
            [wombats-web-client.components.simulator.output
             :as simulator-output]
            [wombats-web-client.components.simulator.stack-trace
             :as simulator-stack-trace]
            [wombats-web-client.components.simulator.configure
             :refer [configuration-modal]]
            [wombats-web-client.components.simulator.controls
             :as simulator-controls]
            [wombats-web-client.events.simulator
             :refer [get-simulator-templates]]
            [wombats-web-client.components.simulator.split-pane :as split-pane]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Lifecycle Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- component-will-mount! []
  (get-simulator-templates))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Render Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- render-right-pane
  [stack-trace]
  [:div {:class-name "right-pane"}
   (split-pane/render [simulator-code/render] [simulator-output/render])])

(defn- render-left-pane
  [{:keys [frame simulator-state simulator-frames simulator-index]}]
  [:div.left-pane
   [simulator-arena/render frame]
   [simulator-controls/render simulator-state simulator-frames simulator-index]])

(defn- get-mini-map-bool
  [simulator-view-mode]
  (if (= simulator-view-mode :self)
    true
    false))

(defn- get-active-frame
  [frames index]
  (println index)
  (get-in (get frames (dec index)) [:game/frame :frame/arena]))

(defn- get-simulator-state
  [frames index]
  (get frames index))

(defn- render
  [{:keys [templates
           wombats
           active-frame
           stack-trace
           simulator-mini-map
           simulator-state
           simulator-view-mode
           simulator-frames
           simulator-index]}]

  (println simulator-state)
  [:div {:class-name "simulator-panel"}
   [render-left-pane {:frame
                      (get-active-frame simulator-frames simulator-index)
                      :simulator-state simulator-state
                      :simulator-frames simulator-frames
                      :simulator-index simulator-index}]
   [render-right-pane stack-trace]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main Method
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn simulator []
  (reagent/create-class
   {:component-will-mount #(component-will-mount!)
    :props-name "simulator-panel"
    :reagent-render
    #(render {:templates @(re-frame/subscribe
                           [:simulator/templates])
              :wombats @(re-frame/subscribe
                         [:my-wombats])
              :active-frame @(re-frame/subscribe
                              [:simulator/active-frame])
              :stack-trace @(re-frame/subscribe
                             [:simulator/player-stack-trace])
              :simulator-mini-map @(re-frame/subscribe
                                    [:simulator/mini-map])
              :simulator-state (re-frame/subscribe
                                [:simulator/state])
              :simulator-view-mode @(re-frame/subscribe
                                     [:simulator/get-view-mode])
              :simulator-frames @(re-frame/subscribe
                                  [:simulator/frames])
              :simulator-index @(re-frame/subscribe
                                 [:simulator/frame-index])})}))
