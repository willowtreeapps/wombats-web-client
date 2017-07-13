(ns wombats-web-client.components.ace
  (:require [reagent.core :as reagent]))

(defn- init-ace [{:keys [code mode id options event-handler editor-atom]}]
  (let [editor (-> js/window
                   .-ace
                   (.edit id))]
    (set! (.-$blockScrolling editor) js/Infinity)
    (reset! editor-atom editor)
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

(defn- refresh-ace
  [next-props editor]
  (let [ace-element @editor]
    (when ace-element
      (let [position (.getCursorPosition ace-element)
            code (:code (get next-props 1))]
        (when code
          (-> ace-element
              .getSession
              (.setValue code)))
        (.resize ace-element)
        (.moveCursorToPosition ace-element position)))))

(defn ace-component
  "Takes a settings map and creates an Ace component with the specified settings
  See wombats-web-client.components.simulator.code for an example."
  [settings]
  (let [editor-atom (reagent/atom nil)
        new-settings (assoc settings :editor-atom editor-atom)]
       (reagent/create-class
        {:reagent-render
         (fn [{:keys [id]}]
           [:div {:id id}])
         :component-did-mount
         #(init-ace new-settings)
         :component-will-receive-props
         (fn [_ next-props editor-atom]
           (refresh-ace next-props (:editor-atom new-settings)))})))
