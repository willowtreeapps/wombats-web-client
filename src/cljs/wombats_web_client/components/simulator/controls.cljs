(ns wombats-web-client.components.simulator.controls
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.utils.socket :as ws]
            [wombats-web-client.components.simulator.configure
             :refer [open-configure-simulator-modal]]
            [wombats-web-client.components.add-button :as add-wombat-button]))

(defn- on-step-click!
  [evt sim-state]
  (re-frame/dispatch [:simulator/process-simulation-frame
                      {:game-state @sim-state}]))

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
  [sim-state show-mini-map]
  [:div.simulator-controls

   [:button.mini-map
    {:on-click #(re-frame/dispatch [:simulator/toggle-simulator-mini-map])}
    (if show-mini-map
      "Show Full View"
      "Show Wombat View")]
   [arrow-button #(on-step-click! % sim-state) "right"]
   [settings-button (open-configure-simulator-modal)]
   ])
