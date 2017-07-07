(ns wombats-web-client.components.simulator.controls
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.utils.socket :as ws]
            [wombats-web-client.components.simulator.configure
             :refer [open-configure-simulator-modal]]
            [wombats-web-client.components.add-button :as add-wombat-button]))

(defn- get-bar-percentage
  [sim-frames sim-index]
  (* 100 (/ sim-index (count sim-frames))))

(def play-status (reagent/atom "paused"))

(defn- play-pause! [evt status]
  (if (= status "paused")
    (do ;;do stuff
      (reset! play-status "playing"))
    (do ;;other stuff
      (reset! play-status "paused"))))

(defn- play-button [status]
  (println status)
  [:img.icon-play
   {:on-click #(play-pause! % status)
    :src (if (= @play-status "paused")
           "/images/icon-play.svg"
           "/images/icon-pause.svg")}])


(defn- progress-bar
  [sim-frames sim-index]
  [:div.progress-bar
   [:div.progress-bar.filled
    {:style {:width (str (get-bar-percentage sim-frames sim-index) "%")}}
    [:div.scrubber]]])

(defn- forward-button!
  [evt sim-state sim-frames sim-index]
  (if (>= sim-index (count sim-frames))
    (re-frame/dispatch [:simulator/process-simulation-frame
                        {:game-state @sim-state}])
    (re-frame/dispatch [:simulator/forward-frame])))

(defn- back-button!
  [evt sim-index]
  (println sim-index)
  (if (> sim-index 0)
    (re-frame/dispatch [:simulator/back-frame])
    (println "Nowhere to go")))

(defn- settings-button
  [on-click]
  [:img.icon-settings
   {:on-click on-click
    :src "/images/icon-settings.svg"}])

(defn- arrow-button
  [on-click orientation]
  [:img.icon-arrow
   {:class orientation
    :on-click on-click
    :src "/images/icon-arrow-left.svg"}])

(defn render
  [sim-state sim-frames sim-index]
  [:div.simulator-controls
   [play-button @play-status]
   [progress-bar sim-frames sim-index]
   [arrow-button #(back-button! % sim-index) "left"]
   [arrow-button #(forward-button! % sim-state sim-frames sim-index) "right"]
   [settings-button (open-configure-simulator-modal)]])
