(ns wombats-web-client.components.simulator.code
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]))


(defn- on-code-change! [evt]
  ;; Propogate the updated code into db
  (re-frame/dispatch [:simulator/update-code evt.target.value]))

(defn- render!
  [code]
  [:textarea#editor {:on-change #(on-code-change! %)
                     :value (or code "")}])

(defn render
  []
  (let [sim-code (re-frame/subscribe [:simulator/code])]
    (render! @sim-code)))
