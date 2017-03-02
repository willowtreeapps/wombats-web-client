(ns wombats-web-client.components.tabbed-container
  (:require [reagent.core :as reagent]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Render Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- render-tabs [{:keys [tabs index on-index-change]}]
  (map-indexed (fn [tab-index {:keys [label]}] 
                 [:button {:key label
                           :class-name (when (= index tab-index)
                                         "active")
                           :onClick #(on-index-change tab-index)} 
                  label])
               tabs))

(defn- render [{:keys [tabs index on-index-change] :as props}]
  (let [render (:render (tabs index))]
    [:div {:class-name "tabbed-container"}
     
     [:div {:class-name "content"} 
      [render]]
     
     [:div {:class-name "tabs"}
      (render-tabs props)]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main Method
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn tabbed-container [props]
  (render props))
