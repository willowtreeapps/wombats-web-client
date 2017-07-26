(ns wombats-web-client.panels.simulator
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.components.arena
             :as arena]
            [wombats-web-client.components.simulator.arena
             :as simulator-arena]
            [wombats-web-client.components.simulator.code
             :as simulator-code]
            [wombats-web-client.components.simulator.configure
             :refer [configuration-panel]]
            [wombats-web-client.components.simulator.controls
             :as simulator-controls]
            [wombats-web-client.components.simulator.output
             :as simulator-output]
            [wombats-web-client.components.simulator.split-pane
             :as split-pane]
            [wombats-web-client.components.simulator.stack-trace
             :as simulator-stack-trace]
            [wombats-web-client.constants.ui
             :refer [mobile-window-width
                     controls-height]]
            [wombats-web-client.events.simulator
             :refer [get-simulator-templates]]
            [wombats-web-client.components.tabbed-container
             :refer [tabbed-container]]))

(defonce root-class "simulator-panel")
(defonce canvas-container-id "left-pane")
(defonce canvas-id "simulator-canvas")


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Helper Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- resize-canvas [simulator-view-mode simulator-data]
  (let [root-element (first
                      (array-seq
                       (.getElementsByClassName
                        js/document
                        root-class)))
        container-element (.getElementById js/document canvas-container-id)
        canvas-element (.getElementById js/document canvas-id)
        width (.-offsetWidth root-element)
        half-width (/ width 2)
        height (- (.-offsetHeight root-element) controls-height)
        mobile-mode (< width mobile-window-width)
        dimension (if mobile-mode
                    width
                    (min height half-width))]

    ;; Check to see if canvas should be rendered in mobile mode
    (set! (.-width canvas-element) dimension)
    (set! (.-height canvas-element) dimension)
    (arena/arena (@simulator-view-mode @simulator-data) canvas-id)))

(defn- on-resize [simulator-data simulator-view-mode]
  (resize-canvas simulator-view-mode simulator-data)
  (js/setTimeout #(resize-canvas simulator-view-mode simulator-data)
                 100))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Lifecycle Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- component-will-mount! []
  (get-simulator-templates))

(defn- component-did-mount
  [{:keys [simulator-data simulator-view-mode resize-fn]}]
  ;; Add resize listener
  (.addEventListener js/window
                     "resize"
                     @resize-fn)
  (resize-canvas simulator-view-mode simulator-data))

(defn- component-will-unmount [resize-fn]
  (.removeEventListener js/window
                        "resize"
                        @resize-fn))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Render Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- render-right-pane
  [{:keys [simulator-data update-sim pane-label]}]
  (let [bottom-pane (if (:player-stack-trace @simulator-data)
                      (do (reset! pane-label "Stack Trace")
                          [simulator-stack-trace/render
                           simulator-data])
                      (do (reset! pane-label "Debug Console")
                          [simulator-output/render
                           simulator-data update-sim]))]
    [:div {:class-name "right-pane"}
     [split-pane/render [simulator-code/render {:simulator-data  simulator-data
                                                :update update-sim}]
      bottom-pane
      update-sim pane-label]]))

(defn- render-left-pane
  [{:keys
    [simulator-view-mode simulator-data simulator-frames simulator-index]}]
  [:div.left-pane {:id "left-pane"}
   [simulator-arena/render simulator-data simulator-view-mode]
   [simulator-controls/render simulator-data simulator-frames simulator-index]])

(defn- render
  [{:keys [update-sim
           pane-label
           simulator-view-mode
           simulator-frames
           simulator-index
           simulator-data]}]
  [:div {:class-name "simulator-panel"}
   ;;(arena/arena (@simulator-view-mode @simulator-data) canvas-id)
   [render-left-pane {:simulator-view-mode @simulator-view-mode
                      :simulator-data simulator-data
                      :simulator-frames simulator-frames
                      :simulator-index simulator-index}]
   [render-right-pane {:simulator-data simulator-data
                       :update-sim update-sim
                       :pane-label pane-label}]])

(defn- config-panel []
   [:div.configuration-container
    [configuration-panel]])

(defn- simulator-panel
  [{:keys [update-sim
           pane-label
           simulator-data
           simulator-view-mode
           simulator-index
           resize-fn]}]
  (reagent/create-class
   {:component-did-mount #(component-did-mount
                           {:simulator-data simulator-data
                            :simulator-view-mode simulator-view-mode
                            :resize-fn resize-fn})
    :component-will-unmount #(component-will-unmount resize-fn)

    :props-name "simulator-panel"
    :reagent-render
    #(render {:update-sim update-sim
              :pane-label pane-label
              :simulator-view-mode simulator-view-mode
              :simulator-frames (re-frame/subscribe
                                 [:simulator/frames])
              :simulator-data simulator-data
              :simulator-index simulator-index})}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main Method
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn simulator []
  (let [update-sim (reagent/atom false)
        pane-label (reagent/atom "Debug Console")
        simulator-data (re-frame/subscribe
                        [:simulator/get-data])
        simulator-view-mode (re-frame/subscribe
                             [:simulator/get-view-mode])
        simulator-index (re-frame/subscribe
                         [:simulator/frame-index])
        resize-fn (reagent/atom #(on-resize simulator-data
                                            simulator-view-mode))]

    (reagent/create-class
     {:component-will-mount #(component-will-mount!)
      :reagent-render
      #(if (neg? @simulator-index)
         [config-panel]
         [simulator-panel {:update-sim update-sim
                           :pane-label pane-label
                           :simulator-data simulator-data
                           :simulator-view-mode simulator-view-mode
                           :simulator-index simulator-index
                           :resize-fn resize-fn}])})))
