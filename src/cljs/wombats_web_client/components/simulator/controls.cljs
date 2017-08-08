(ns wombats-web-client.components.simulator.controls
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.components.simulator.configure
             :refer [open-configure-simulator-modal]]
            [wombats-web-client.components.simulator.progress-bar
             :as progress-bar]
            [wombats-web-client.constants.games :refer [simulator-frame-time]]
            [wombats-web-client.utils.socket :as ws]
            [wombats-web-client.utils.time :as time]))

(defn- on-back-button-click!
  [evt index]
  (when (pos? @index)
    (re-frame/dispatch [:simulator/back-frame])))

(defn- on-forward-button-click!
  [evt {:keys [simulator-data frames index]}]
  (if (>= @index (dec (count @frames)))
    (re-frame/dispatch [:simulator/process-simulation-frame
                        {:game-state (:state @simulator-data)}])
    (re-frame/dispatch [:simulator/forward-frame])))

(defn- settings-button
  [{:keys [on-click]}]
  [:button.simulator-button.settings {:on-click on-click}
   [:img.icon-settings
    {:src "/images/icon-settings.svg"}]])

(defn- play-button
  [{:keys [simulator-data frames index interval play-status]}]
  [:button.simulator-button.play
   {:on-click
    (fn [] (if (= (:play-status @play-status) :paused)
            (re-frame/dispatch
             [:simulator/play-state
              {:play-status :playing
               :interval (js/setInterval
                          #(on-forward-button-click!
                            % {:simulator-data simulator-data
                               :frames frames
                               :index index})
                          simulator-frame-time)}])
            (do
              (js/clearInterval (:interval @play-status))
              (re-frame/dispatch [:simulator/play-state {:play-status  :paused
                                                         :interval nil}]))))}
   [:img.icon-play
    {:src (if (= (:play-status @play-status) :paused)
            "/images/icon-play.svg"
            "/images/icon-pause.svg")}]])

(defn- arrow-button
  [{:keys [on-click orientation]}]
  [:button.simulator-button.arrow {:on-click on-click}
   [:img.icon-arrow
    {:class orientation
     :src "/images/icon-arrow-left.svg"}]])

(defn render
  [simulator-data frames index]
  (let [interval (reagent/atom 0)
        play-status (re-frame/subscribe [:simulator/play-state])]
    [:div.simulator-controls
     [play-button {:simulator-data simulator-data
                   :frames frames
                   :index index
                   :interval interval
                   :play-status play-status}]
     [progress-bar/render frames index]
     [arrow-button {:on-click  #(on-back-button-click! % index)
                    :orientation "left"}]
     [arrow-button {:on-click
                    #(on-forward-button-click! % {:simulator-data simulator-data
                                                  :frames frames
                                                  :index index})
                    :orientation "right"}]
     [settings-button {:on-click  open-configure-simulator-modal}]]))
