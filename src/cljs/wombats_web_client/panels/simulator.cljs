(ns wombats-web-client.panels.simulator
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.components.arena :as arena]
            [wombats-web-client.utils.socket :as ws]))

(defonce canvas-id "simulator-canvas")
(defonce dimensions 600)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Lifecycle Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- component-will-mount []
  ;; Pull these out properly
  (re-frame/dispatch [:simulator/initialize {:simulator-template-id "c0818a41-f177-446e-9f26-02936032eba8"
                                             :wombat-id "11d28d6e-e0cc-422f-9f9e-c7ab84c7ebe5"}]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Accessors
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- get-player-code [state]
  (let [player (last (first (:players state)))]
    (get-in player [:state :code :code])))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Callback Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- on-step-click [evt state]
  (ws/send-message :process-simulation-frame {:game-state state}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Render Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- render-left-pane [state]
  (let [arena-frame (get-in state [:frame :frame/arena])]
    (arena/arena arena-frame canvas-id)
    [:div {:class-name "left-pane"}
     [:canvas {:id canvas-id
               :width dimensions
               :height dimensions}]]))

(defn- render-right-pane [state]
  [:div {:class-name "right-pane"}
   [:textarea {:value (or  (get-player-code state) "")}]
   [:button {:onClick #(on-step-click % state)} 
    "Step"]])

(defn- render [state]
  [:div {:class-name "simulator-panel"}
   [render-left-pane state]
   [render-right-pane state]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main Method
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn simulator []
  (let [state (re-frame/subscribe [:simulator/state])]
    (reagent/create-class
     {:component-will-mount #(component-will-mount)
      :display-name "simulator-panel"
      :reagent-render #(render @state)})))
