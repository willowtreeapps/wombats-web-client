(ns wombats-web-client.components.simulator.split-pane
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [goog.events :as events]
            [wombats-web-client.constants.ui
             :refer [navbar-height
                     controls-height]]
            [wombats-web-client.utils.functions
             :refer [get-mobile-status]])
  (:import [goog.events EventType]))

(defonce split-pane-id "split-pane")
(defonce max-height 107)

(defn- mouse-move-handler [{:keys [update top-size-px]}]
  (fn [evt]
    (let [split-pane-element (.getElementById js/document split-pane-id)
          y-offset (.-top  (.getBoundingClientRect split-pane-element))
          y (- (.-clientY evt) y-offset)]

      (if (> y (- js/innerHeight max-height))
        (reset! top-size-px (- js/innerHeight max-height))
        (reset! top-size-px y))
      (reset! update (not @update)))))

(defn- mouse-down-handler [e {:keys [update top-size-px]}]
  (let [on-move (mouse-move-handler {:update update
                                     :top-size-px top-size-px})]
    (.preventDefault e)
    (events/listen js/window EventType.MOUSEMOVE
                   on-move)
    (events/listen js/window EventType.MOUSEUP
                   #(events/unlisten
                     js/window EventType.MOUSEMOVE on-move))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Lifecycle Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- render-divider [{:keys [divider-text update top-size-px]}]
  [:div.panel-divider {:id "split-pane-divider"
                       :on-mouse-down
                       #(mouse-down-handler % {:update update
                                               :top-size-px top-size-px})}
   [:p.panel-divider-text @divider-text]
   [:hr.panel-grabber]])

(defn render
  ([top bottom update]
   (render top bottom update ""))
  ([top bottom update divider-text]
   (let [top-size-px (reagent/atom 145)]
     (reagent/create-class
      {:display-name "split-pane"
       :reagent-render (fn [top bottom update]
                         [:div.split-panel {:id "split-pane"}

                          ;; trigger rerender on resize
                          @update
                          [:div.panel-top
                           {:style {:height (str @top-size-px "px")}} top]
                          [render-divider {:divider-text divider-text
                                           :update update
                                           :top-size-px top-size-px}]
                          [:div.panel-bottom
                           bottom]])}))))
