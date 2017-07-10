(ns wombats-web-client.components.simulator.controls
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.utils.socket :as ws]
            [wombats-web-client.components.simulator.configure
             :refer [open-configure-simulator-modal]]
            [wombats-web-client.components.simulator.progress-bar :as progress-bar]
            [wombats-web-client.utils.time :as time]))
(defn- back-button!
  [evt sim-index]
  (if (> sim-index 0)
    (re-frame/dispatch [:simulator/back-frame])
    (println "Nowhere to go")))

(defn- forward-button!
  [evt sim-state sim-frames sim-index]
  (if (>= sim-index (count sim-frames))
    (re-frame/dispatch [:simulator/process-simulation-frame
                        {:game-state @sim-state}])
    (re-frame/dispatch [:simulator/forward-frame])))

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
   [progress-bar/render sim-frames sim-index]
   [arrow-button #(back-button! % sim-index) "left"]
   [arrow-button #(forward-button! % sim-state sim-frames sim-index) "right"]
   [settings-button (open-configure-simulator-modal)]])
