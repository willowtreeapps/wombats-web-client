(ns wombats-web-client.panels.simulator
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.components.arena :as arena]
            [wombats-web-client.components.tabbed-container :refer [tabbed-container]]
            [wombats-web-client.components.simulator.arena :as simulator-arena]
            [wombats-web-client.components.simulator.code :as simulator-code]
            [wombats-web-client.components.simulator.output :as simulator-output]
            [wombats-web-client.components.simulator.stack-trace :as simulator-stack-trace]
            [wombats-web-client.components.simulator.configure :as simulator-configure]
            [wombats-web-client.components.simulator.controls :as simulator-controls]
            [wombats-web-client.events.simulator :refer [get-simulator-templates]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Lifecycle Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- component-will-mount! []
  (get-simulator-templates))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Render Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def panes {:code
            {:label "CODE"
             :render simulator-code/render}
            :output
            {:label "OUTPUT"
             :render simulator-output/render}
            :debugger
            {:label "DEBUGGER"
             :render simulator-stack-trace/render}
            :configure
            {:label "CONFIGURE"
             :render simulator-configure/render}})

(defn- render-tabs
  [active-tab stack-trace]
  (for [[tab-name {label :label}] panes]
    ^{:key label}
    [:button.tab-btn {:class (when (= tab-name active-tab) "active-line-top")
                      :on-click #(re-frame/dispatch [:simulator/update-active-simulator-pane tab-name])}
     label
     (when (= tab-name :debugger)
       (when stack-trace
         [:span.notifications 1]))]))

(defn- render-right-pane
  [active-pane stack-trace]
  (let [{pane-render :render} (active-pane panes)]
    [:div {:class-name "right-pane"}
     [:div.tabbed-container
      [:div.content
       [pane-render]]
      [:div.tabs
       (render-tabs active-pane stack-trace)]]]))

(defn- render
  [sim-pane
   templates
   wombats
   active-frame
   stack-trace
   simulator-display-mini-map
   simulator-mini-map
   simulator-state]
  [:div {:class-name "simulator-panel"}
   [simulator-arena/render (if (and simulator-display-mini-map
                                    simulator-mini-map)
                             simulator-mini-map
                             active-frame)]
   [render-right-pane @sim-pane stack-trace]
   [simulator-controls/render simulator-state simulator-display-mini-map]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main Method
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn simulator []
  (let [simulator-pane (re-frame/subscribe [:simulator/active-pane])
        templates (re-frame/subscribe [:simulator/templates])
        wombats (re-frame/subscribe [:my-wombats])
        active-frame (re-frame/subscribe [:simulator/active-frame])
        stack-trace (re-frame/subscribe [:simulator/player-stack-trace])
        simulator-display-mini-map (re-frame/subscribe [:simulator/display-mini-map])
        simulator-mini-map (re-frame/subscribe [:simulator/mini-map])
        simulator-state (re-frame/subscribe [:simulator/state])]
    (reagent/create-class
     {:component-will-mount #(component-will-mount!)
      :props-name "simulator-panel"
      :reagent-render #(render simulator-pane
                               @templates
                               @wombats
                               @active-frame
                               @stack-trace
                               @simulator-display-mini-map
                               @simulator-mini-map
                               simulator-state)})))
