(ns wombats-web-client.components.simulator.output
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.components.ace :refer [ace-component]]))

(defn- format-code
  [clj-object]
  (str (.stringify js/JSON (clj->js clj-object) nil 2)))

(defn- on-change [evt]
  (reagent/force-update-all))

(defn render [{:keys [command player-state update]}]
  [:div.output-container
   [:div.output-section
    [:h4.output-section-title "Command"]]
   [ace-component  {:code (format-code @command)
                    :mode "json"
                    :id "command"
                    :options {:readOnly true
                              :highlightActiveLine false
                              :minLines 6
                              :maxLines 7}}]
   [:div.output-section
    [:h4.output-section-title "State"]]

   [ace-component {:code (format-code @player-state)
                   :mode "json"
                   :id "state"
                   :update @update
                   :options {:readOnly true
                             :highlightActiveLine false}}]])
