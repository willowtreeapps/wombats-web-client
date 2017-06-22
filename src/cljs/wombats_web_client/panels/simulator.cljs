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
  [{:keys [frame simulator-state]}]
  [:div.left-pane
   [simulator-arena/render frame]
   [simulator-controls/render simulator-state]])

(defn- get-mini-map-bool
  [simulator-view-mode]
  (if (= simulator-view-mode :self)
    true
    false))

(defn- render
  [{:keys [templates
           wombats
           active-frame
           stack-trace
           simulator-mini-map
           simulator-state
           simulator-view-mode]}]

  [:div {:class-name "simulator-panel"}
   [render-left-pane {:frame (if (and (get-mini-map-bool simulator-view-mode)
                                      simulator-mini-map)
                               simulator-mini-map
                               active-frame)
                      :simulator-state simulator-state}]
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
                                     [:simulator/get-view-mode])})}))
