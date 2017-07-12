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
    (js/Math.floor (* (/ bound-x 400) (count sim-frames)))))

(defn- get-bar-percentage
  [sim-frames sim-index]
  (bind-value (* 100 (/ @sim-index (count @sim-frames))) 0 100))

(defn- mouse-move-handler [offset sim-frames sim-index]
  (fn [evt]
    (.preventDefault evt)
    (let [x (- (.-clientX evt) (:x offset))
          bar-index (get-bar-index x @sim-frames)]
      (when (> @sim-index bar-index)
        (re-frame/dispatch [:simulator/back-frame]))
      (when (< @sim-index bar-index)
        (re-frame/dispatch [:simulator/forward-frame])))))

(defn- mouse-up-handler [on-move]
  (fn [evt]
    (events/unlisten js/window EventType.MOUSEMOVE
                     on-move)))

(defn- mouse-down-handler [e sim-frames sim-index]
  (let [offset             {:x play-button-width
                            :y  0}
        on-move            (mouse-move-handler offset sim-frames sim-index)]

    (events/listen js/window EventType.MOUSEMOVE
                   on-move)
    (events/listen js/window EventType.MOUSEUP
                   (mouse-up-handler on-move))))

(defn render
  [sim-frames sim-index]
  [:div.progress-bar
   [:div.progress-bar.filled
    {:style {:width (str (get-bar-percentage sim-frames sim-index) "%")}}
    [:div.scrubber
     {:on-mouse-down #(mouse-down-handler % sim-frames sim-index)}]]])
