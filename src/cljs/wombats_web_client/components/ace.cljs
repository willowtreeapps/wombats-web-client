(ns wombats-web-client.components.ace
  (:require [reagent.core :as reagent]))

(defn- init-ace [{:keys [code mode id options event-handler]}]
  (let [editor (-> js/window
                   .-ace
                   (.edit id))]

    (when code (-> editor
                   .getSession
                   (.setValue code)))

    (when event-handler
      (.on editor "change" (event-handler editor)))

    (when mode (-> editor
                   .getSession
                   (.setMode (str "ace/mode/" mode))))
    (when options
      (.setOptions editor (clj->js options)))
    (.setTheme editor "ace/theme/tomorrow_night_eighties")))

(defn ace-component
  "Takes a settings map and creates an Ace component with the specified settings
  See wombats-web-client.components.simulator.code for an example."
  [settings]
  (reagent/create-class
   {:reagent-render
    (fn [{:keys [id]}] [:div {:id id}])
    :component-did-mount
    #(init-ace settings)}))
