(ns wombats-web-client.components.simulator.controls
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.utils.socket :as ws]
            [wombats-web-client.components.simulator.configure
             :refer [open-configure-simulator-modal]]
            [wombats-web-client.components.simulator.progress-bar :as progress-bar]
            [wombats-web-client.utils.time :as time]))

(defn- back-button!
  [evt index]
  (if (> @index 0)
    (re-frame/dispatch [:simulator/back-frame])
    (println "Nowhere to go")))

(defn- forward-button!
  [evt simulator-data frames index]
  (if (>= @index (count @frames))
    (re-frame/dispatch [:simulator/process-simulation-frame
                        {:game-state (:state @simulator-data)}])
    (re-frame/dispatch [:simulator/forward-frame])))

(defn- settings-button
  [on-click]
  [:img.icon-settings
   {:on-click on-click
    :src "/images/icon-settings.svg"}])


(defonce interval (reagent/atom 0))
(defonce play-status (reagent/atom "paused"))
(defonce frame-time 1000)

(defn- play-button [{:keys [simulator-data frames index]}]
  [:img.icon-play
   {:on-click (fn [] (if (= @play-status "paused")
                      (do
                        (reset! play-status "playing")
                        (reset! interval
                                (js/setInterval
                                 #(forward-button! % simulator-data frames index)
                                 frame-time)))
                      (do
                        (js/clearInterval @interval)
                        (reset! play-status "paused"))))
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
  [simulator-data frames index]
  [:div.simulator-controls
   [play-button {:simulator-data simulator-data
                 :frames frames
                 :index index}]
   [progress-bar/render frames index]
   [arrow-button #(back-button! % index) "left"]
   [arrow-button #(forward-button! % simulator-data frames index) "right"]
   [settings-button (open-configure-simulator-modal)]])
