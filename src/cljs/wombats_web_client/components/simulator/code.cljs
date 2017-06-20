(ns wombats-web-client.components.simulator.code
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.components.ace :refer [ace-component]]))

(defn- on-code-change! [editor]
  (fn []
    ;; Propogate the updated code into db
    (re-frame/dispatch [:simulator/update-code (.getValue editor)])))

(defn render []
  (let [code (re-frame/subscribe [:simulator/code])
        mode (re-frame/subscribe [:simulator/code-mode])]
    [ace-component {:code @code
                    :mode @mode
                    :id "editor"
                    :event-handler on-code-change!}]))
