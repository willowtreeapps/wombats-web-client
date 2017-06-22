(ns wombats-web-client.components.simulator.split-pane
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [goog.events :as events])
  (:import [goog.events EventType]))


(defn get-client-rect [evt]
  (let [r (.getBoundingClientRect (.-target evt))]
    {:left (.-left r), :top (.-top r)}))

(def top-size-px (reagent/atom 145))

(defn mouse-move-handler [offset]
  (fn [evt]
    (let [x (- (.-clientX evt) (:x offset))
          y (- (.-clientY evt) (:y offset))
          max-height 200]

      (if (> y (- js/innerHeight max-height))
        (reset! top-size-px (- js/innerHeight max-height))
        (reset! top-size-px y))

      ;; Run the movement handler here to change the styles
      )))


(defn mouse-up-handler [on-move]
  (fn me [evt]
    (events/unlisten js/window EventType.MOUSEMOVE
                     on-move)))

(defn mouse-down-handler [e]
  (let [{:keys [left top]} (get-client-rect e)
        offset             {:x (- (.-clientX e) left)
                            :y (- (.-clientY e) top)}
        on-move            (mouse-move-handler offset)]
    (events/listen js/window EventType.MOUSEMOVE
                   on-move)
    (events/listen js/window EventType.MOUSEUP
                   (mouse-up-handler on-move))))


(defn- render-divider []
  [:div [:hr.panel-divider
         {:on-mouse-down mouse-down-handler}]
   [:hr.panel-grabber]])


(defn render [top bottom]
  [:div.split-panel
   [:div.panel-top {:style {:height (str @top-size-px "px")}} top]
   [render-divider]

   [:div.panel-bottom
    bottom]])
