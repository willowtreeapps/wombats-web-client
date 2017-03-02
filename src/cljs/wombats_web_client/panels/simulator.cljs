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
  (get-simulator-templates))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Accessors
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- get-player [sim-state]
  (let [players (:players @sim-state)
        player-key (first (keys players))]
    (get-in players [player-key :state])))

(defn- get-player-state [sim-state]
  (:saved-state (get-player sim-state)))

(defn- get-player-command [sim-state] 
  (:command (get-player sim-state)))

(defn- get-player-code [sim-state]
  (get-in (get-player sim-state)
          [:code :code]))

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
  [cmpnt-state templates wombats]
  (swap! cmpnt-state assoc :initialized? true)
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
  [:textarea#editor {:on-change #(on-code-change! %)
                     :value (or (get-player-code state) "")}])

(defn- render-output-tab [sim-state]
  [:div.output
   
   [:div.output-section
    [:h3.output-section-title "Command"]
    (prn-str (get-player-command sim-state))]
   
   [:div.output-section
    [:h3.output-section-title "State"]
    (prn-str (get-player-state sim-state))]])

(defn- render-tabbed-container [cmpnt-state sim-state]
  [tabbed-container {:tabs [{:label "CODE"
                             :render #(render-code-tab sim-state)}
                            {:label "OUTPUT"
                             :render #(render-output-tab sim-state)}]
                     :index (:tab-index @cmpnt-state)
                     :on-index-change #(swap! cmpnt-state assoc :tab-index %)}])

(defn- render-right-pane [cmpnt-state sim-state]
  [:div {:class-name "right-pane"}
   (render-tabbed-container cmpnt-state sim-state)
   [:button {:on-click #(on-step-click! % sim-state)}
    "Step"]])

(defn- render! [cmpnt-state sim-state templates wombats]
  (when (and (not (:initialized? @cmpnt-state)) @templates @wombats)
    (initialize-simulator! cmpnt-state templates wombats))

  [:div {:class-name "simulator-panel"}
   [render-left-pane! sim-state]
   [render-right-pane cmpnt-state sim-state]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main Method
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn simulator []
  (let [cmpnt-state (reagent/atom {:tab-index 0
                                   :initialized? false})
        sim-state (re-frame/subscribe [:simulator/state])
        sim-templates (re-frame/subscribe [:simulator/templates])
        wombats (re-frame/subscribe [:my-wombats])]
    (reagent/create-class
     {:component-will-mount #(component-will-mount!)
      :props-name "simulator-panel"
      :reagent-render #(render! cmpnt-state
                                sim-state 
                                sim-templates
                                wombats)})))
