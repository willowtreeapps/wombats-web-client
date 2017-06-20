(ns wombats-web-client.components.simulator.output
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]))

(defn- format-code
  [clj-object]
  (.stringify js/JSON (clj->js clj-object) nil 2))

(defn- init-ace [code mode id]
  (let [editor (-> js/window
                   .-ace
                   (.edit id))]

    (when code (-> editor
                    .getSession
                    (.setValue code)))

    (when mode (-> editor
                    .getSession
                    (.setMode (str "ace/mode/" mode))))
    (when (= id "command")
      (.setAutoScrollEditorIntoView editor true)
      (.setOption editor "maxLines" 7))
    (.setTheme editor "ace/theme/tomorrow_night_eighties")
    (.setReadOnly editor false)))

(defn- render-editor
  [code id]
  [:div.output-display {:id id}])

(defn render-ace
  [code id]
  (reagent/create-class
   {:reagent-render
    #(render-editor code id)
    :component-did-mount
    #(init-ace code "json" id)}))

(defn render []
  (let [command (re-frame/subscribe [:simulator/player-command])
        player-state (re-frame/subscribe [:simulator/player-state])]
    [:div.output-container
     [:div.output-section
      [:h3.output-section-title "Command"]]
     [render-ace (str (format-code @command)) "command"]
     [:div.output-section
      [:h3.output-section-title "State"]]
     [render-ace (str (format-code @player-state)) "state"]]
    ))


#_[:div.output
     [:div.output-section
      [:h3.output-section-title "Command"]
      (format-code @command)]

     [:div.output-section
      [:h3.output-section-title "State"]
      (format-code @player-state)]]
