(ns wombats-web-client.components.radio-select
  (:require [reagent.core :as reagent]))

(defn radio-input [radio key selected-val state]
  (let [is-selected (when (= radio selected-val))]
    [:div.radio-input {:on-click #(swap! state assoc key radio)}
     [:div.radio {:class (when (= radio selected-val) "selected")}]
     [:span.desc radio]]))

(defn radio-select
  [{:keys [class name label radios state]}]
  (let [key (keyword name)
        selected-val (get @state key)]
    [:div.radio-select-wrapper {:class class}
     [:label.label {:for name} label]
     (for [radio radios]
       ^{:key radio}
       [radio-input radio key selected-val state])]))
