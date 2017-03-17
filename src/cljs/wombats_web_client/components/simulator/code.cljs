(ns wombats-web-client.components.simulator.code
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]))

(defn- on-code-change! [editor]
  (fn []
    ;; Propogate the updated code into db
    (re-frame/dispatch [:simulator/update-code (.getValue editor)])))

(defn- render-editor
  [code]
  [:div#editor])

(defn- init-ace [code mode]
  (let [editor (-> js/window
                   .-ace
                   (.edit "editor"))]

    (when @code (-> editor
                    .getSession
                    (.setValue @code)))

    (when @code (.on editor "change" (on-code-change! editor)))

    (when @mode (-> editor
                    .getSession
                    (.setMode (str "ace/mode/" @mode))))

    (.setTheme editor "ace/theme/tomorrow_night_eighties")))

(defn render []
  (let [code (re-frame/subscribe [:simulator/code])
        mode (re-frame/subscribe [:simulator/code-mode])]
    (reagent/create-class
     {:reagent-render #(render-editor code)
      :component-did-mount #(init-ace code mode)})))
