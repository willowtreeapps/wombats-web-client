(ns wombats-web-client.panels.simulator
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.components.arena :as arena]
            [wombats-web-client.components.tabbed-container :refer [tabbed-container]]
            [wombats-web-client.events.simulator :refer [get-simulator-templates]]
            [wombats-web-client.utils.socket :as ws]))

(defonce canvas-id "simulator-canvas")
(defonce dimensions 600)

;; TODO: Currently this page always uses the first template and first wombat.
;;       We should hook up UI that allows you to switch between them.

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Lifecycle Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- component-will-mount! []
  (re-frame/dispatch [:simulator/initialized false])
  (get-simulator-templates))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Accessors
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- get-player-code [state]
  (let [players (:players @state)
        player-key (first (keys players))]
    (get-in players [player-key :state :code :code])))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Callback Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- on-code-change! [evt]
  ;; Propogate the updated code into db
  (re-frame/dispatch [:simulator/update-code evt.target.value]))

(defn- on-step-click! [evt state]
  (ws/send-message :process-simulation-frame {:game-state @state}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Helper methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- initialize-simulator!
  [templates wombats]
  (re-frame/dispatch [:simulator/initialized true])
  (ws/send-message :connect-to-simulator 
                   {:simulator-template-id (:simulator-template/id (first @templates))
                    :wombat-id (:wombat/id (first @wombats))}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Render Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- render-left-pane! [state]
  (let [arena-frame (get-in @state [:frame :frame/arena])]
    (arena/arena arena-frame canvas-id)
    [:div {:class-name "left-pane"}
     [:canvas {:id canvas-id
               :width dimensions
               :height dimensions}]]))

(defn- render-code-tab [state]
  (fn []
    [:textarea {:id "editor"
                :onChange #(on-code-change! %)
                :value (or (get-player-code state) "")}]))

(defn- render-output-tab [state]
  [:div "OUTPUT"])

(defn- render-tabbed-container [state]
  [tabbed-container {:tabs [{:label "CODE"
                             :markup (render-code-tab state)}
                            {:label "OUTPUT"
                             :markup (render-output-tab state)}]
                     :index 0
                     :on-index-change nil}])

(defn- render-right-pane [state]
  [:div {:class-name "right-pane"}
   (render-tabbed-container state)
   [:button {:onClick #(on-step-click! % state)}
    "Step"]])

(defn- render! [initialized? state templates wombats]
  (when (and (not @initialized?) @templates @wombats)
    (initialize-simulator! templates wombats))

  [:div {:class-name "simulator-panel"}
   [render-left-pane! state]
   [render-right-pane state]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main Method
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn simulator []
  (let [sim-initialized? (re-frame/subscribe [:simulator/initialized])
        sim-state (re-frame/subscribe [:simulator/state])
        sim-templates (re-frame/subscribe [:simulator/templates])
        wombats (re-frame/subscribe [:my-wombats])]
    (reagent/create-class
     {:component-will-mount #(component-will-mount!)
      :props-name "simulator-panel"
      :reagent-render #(render! sim-initialized? sim-state sim-templates wombats)})))
