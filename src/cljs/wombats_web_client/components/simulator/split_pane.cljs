(ns wombats-web-client.components.simulator.split-pane
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [goog.events :as events])
  (:import [goog.events EventType]))


(defonce navbar-height 45)
(defonce max-height 105)
(defonce top-size-px (reagent/atom 145))

(defn- mouse-move-handler [offset]
  (fn [evt]
    (let [y (- (.-clientY evt) (:y offset))]
      (if (> y (- js/innerHeight max-height))
        (reset! top-size-px (- js/innerHeight max-height))
        (reset! top-size-px y)))))


(defn- mouse-up-handler [on-move]
  (fn me [evt]
    (events/unlisten js/window EventType.MOUSEMOVE
                     on-move)))

(defn- mouse-down-handler [e]
  (let [offset             {:y (+ 0 navbar-height)}
        on-move            (mouse-move-handler offset)]
    (events/listen js/window EventType.MOUSEMOVE
                   on-move)
    (events/listen js/window EventType.MOUSEUP
                   (mouse-up-handler on-move))))

(defn- render-divider []
  [:div {:on-mouse-down mouse-down-handler}
   [:hr.panel-divider]
   [:hr.panel-grabber]])

(defn render [top bottom]
  [:div.split-panel
   [:div.panel-top {:style {:height (str @top-size-px "px")}} top]
   [render-divider]
   [:div.panel-bottom
    bottom]])
