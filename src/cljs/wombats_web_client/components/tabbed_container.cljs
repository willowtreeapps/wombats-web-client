(ns wombats-web-client.components.tabbed-container
  (:require [reagent.core :as reagent]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Render Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- render [{:keys [tabs index on-index-change]}]
  (let [markup (:markup (nth tabs index))]
    [:div {:class-name "tabbed-container"}
     [markup]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main Method
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn tabbed-container [{:keys [tabs index on-index-change] :as props}]
  (reagent/create-class
   {:props-name "tabbed-container"
    :reagent-render #(render props)}))
