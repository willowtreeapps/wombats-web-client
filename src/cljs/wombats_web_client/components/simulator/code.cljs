(ns wombats-web-client.components.simulator.code
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.components.ace :refer [ace-component]]))

(defn- on-code-change! [editor]
  (fn []
    ;; Propogate the updated code into db
    (re-frame/dispatch [:simulator/update-code (.getValue editor)])))

(defn render [{:keys [simulator-data update]}]
  [ace-component {:code (:code @simulator-data)
                  :mode (:code-mode @simulator-data)
                  :id "editor"
                  :update @update
                  :event-handler on-code-change!}])
