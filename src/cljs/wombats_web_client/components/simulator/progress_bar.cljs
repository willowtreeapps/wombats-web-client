(ns wombats-web-client.components.simulator.progress-bar
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.utils.functions :refer [bind-value]]
            [goog.events :as events])
  (:import [goog.events EventType]))

(defonce play-button-width 64)
(defonce progress-bar-width 400)

(defn- get-bar-index
  [x sim-frames]
  (let [bound-x (bind-value x 0 progress-bar-width)]
    (js/Math.floor (* (/ bound-x 400) (count sim-frames))))) ;; might need rounding

(defn- get-bar-percentage
  [sim-frames sim-index]
  ;; TODO fix the bug with the play-pause button making percentages bigger than 100%
  (bind-value (* 100 (/ sim-index (count sim-frames))) 0 100))

(defn- mouse-move-handler [offset]
  (fn [evt]
    (.preventDefault evt)
    (let [x (- (.-clientX evt) (:x offset))
          sim-index @(re-frame/subscribe [:simulator/frame-index])
          sim-frames @(re-frame/subscribe [:simulator/frames])
          bar-index (get-bar-index x sim-frames)]
      (when (and (> sim-index bar-index) (> sim-index 0))
        (re-frame/dispatch [:simulator/back-frame]))
      (when (and (< sim-index bar-index) (< sim-index (count sim-frames)))
        (re-frame/dispatch [:simulator/forward-frame])))))

(defn- mouse-up-handler [on-move]
  (fn me [evt]
    (events/unlisten js/window EventType.MOUSEMOVE
                     on-move)))

(defn- mouse-down-handler [e]
  (let [offset             {:x play-button-width
                            :y  0}
        on-move            (mouse-move-handler offset)]

    (events/listen js/window EventType.MOUSEMOVE
                   on-move)
    (events/listen js/window EventType.MOUSEUP
                   (mouse-up-handler on-move))))

(defn render
  [sim-frames sim-index]
  [:div.progress-bar
   [:div.progress-bar.filled
    {:style {:width (str (get-bar-percentage sim-frames sim-index) "%")}}
    [:div.scrubber {:on-mouse-down mouse-down-handler}]]])
