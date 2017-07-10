(ns wombats-web-client.components.simulator.controls
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.utils.socket :as ws]
            [wombats-web-client.components.simulator.configure
             :refer [open-configure-simulator-modal]]
            [wombats-web-client.components.add-button :as add-wombat-button]
            [wombats-web-client.utils.time :as time]
            [goog.events :as events])
  (:import [goog.events EventType]))

(defonce play-button-width 64)
(defonce progress-bar-width 400)

(def prev-slider-location (reagent/atom 0))

(defn get-client-rect [evt]
  (let [r (.getBoundingClientRect (.-target evt))]
    {:left (.-left r), :top (.-top r)}))

(defn- get-bar-index
  [x]
  (let [sim-frames @(re-frame/subscribe [:simulator/frames])]
       (println (/ x 400))))

(defn mouse-move-handler [offset]
  (fn [evt]
    (let [x (- (.-clientX evt) (:x offset))]
      (when (= @prev-slider-location 0)
        (reset! prev-slider-location x))
      (get-bar-index x)
      (when (< x @prev-slider-location)
        (println "move back a frame")
        (reset! prev-slider-location x))
      (when (> x @prev-slider-location)
        (println "move forward a frame")
        (reset! prev-slider-location x)))))

(defn mouse-up-handler [on-move]
  (fn me [evt]
    (events/unlisten js/window EventType.MOUSEMOVE
                     on-move)))

(defn mouse-down-handler [e]
  (let [{:keys [left top]} (get-client-rect e)
        offset             {:x play-button-width
                            :y  0}
        on-move            (mouse-move-handler offset)]

    (events/listen js/window EventType.MOUSEMOVE
                   on-move)
    (events/listen js/window EventType.MOUSEUP
                   (mouse-up-handler on-move))))



(defn- get-bar-percentage
  [sim-frames sim-index]
  ;; TODO fix the bug with the play-pause button making percentages bigger than 100%
  (* 100 (/ sim-index (count sim-frames))))

(defn- progress-bar
  [sim-frames sim-index]
  [:div.progress-bar
   [:div.progress-bar.filled
    {:style {:width (str (get-bar-percentage sim-frames sim-index) "%")}}
    [:div.scrubber {:on-mouse-down mouse-down-handler}]]])




(defn- forward-button!
  [evt sim-state sim-frames sim-index]
  (if (>= sim-index (count sim-frames))
    (re-frame/dispatch [:simulator/process-simulation-frame
                        {:game-state @sim-state}])
    (re-frame/dispatch [:simulator/forward-frame])))

(defn- back-button!
  [evt sim-index]
  (if (> sim-index 0)
    (re-frame/dispatch [:simulator/back-frame])
    (println "Nowhere to go")))

(defn- settings-button
  [on-click]
  [:img.icon-settings
   {:on-click on-click
    :src "/images/icon-settings.svg"}])


(defonce interval (reagent/atom 0))
(defonce play-status (reagent/atom "paused"))
(defonce frame-time 1000)

(defn- play-pause! [{:keys [sim-state sim-frames sim-index]}]
  (if (= @play-status "paused")
    (do
      (reset! play-status "playing")
      (reset! interval
              (js/setInterval #(forward-button! % sim-state sim-frames sim-index) frame-time)))
    (do
      (js/clearInterval @interval)
      (reset! play-status "paused"))))


(defn- play-button [sim-status]
  [:img.icon-play
   {:on-click #(play-pause! sim-status)
    :src (if (= @play-status "paused")
           "/images/icon-play.svg"
           "/images/icon-pause.svg")}])

(defn- arrow-button
  [on-click orientation]
  [:img.icon-arrow
   {:class orientation
    :on-click on-click
    :src "/images/icon-arrow-left.svg"}])

(defn render
  [sim-state sim-frames sim-index]
  [:div.simulator-controls
   [play-button {:sim-state sim-state
                 :sim-frames sim-frames
                 :sim-index sim-index}]
   [progress-bar sim-frames sim-index]
   [arrow-button #(back-button! % sim-index) "left"]
   [arrow-button #(forward-button! % sim-state sim-frames sim-index) "right"]
   [settings-button (open-configure-simulator-modal)]])
