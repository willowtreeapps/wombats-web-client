(ns wombats-web-client.components.simulator.output
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.components.ace :refer [ace-component]]))

(defn- format-code
  [clj-object]
  (str (.stringify js/JSON (clj->js clj-object) nil 2)))

(defn- refresh-ace!
  [editor]
  (println "refreshing ace")
  (.renderer.updateFull editor))

(def force-refresh (reagent/atom 0))

(defn render [{:keys [command player-state]}]

  [:div.output-container
   [:div.output-section
    [:h4.output-section-title "Command"]]
   (println (str "output"  command))
   [:div command]
   [ace-component  {:code (format-code @command)
                    :mode "json"
                    :id "command"
                    :options {:readOnly true
                              :highlightActiveLine false
                              :maxLines 7}}]
   [:div.output-section
    [:h4.output-section-title "State"]]

   [ace-component {:code (format-code @player-state)
                   :mode "json"
                   :id "state"
                   :options {:readOnly true
                             :highlightActiveLine false}}]])
