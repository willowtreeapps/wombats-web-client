(ns wombats-web-client.components.tabbed-container
  (:require [reagent.core :as reagent]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Render Methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- render-tabs [{:keys [tabs index on-index-change]}]
  (map-indexed (fn [tab-index {:keys [label]}] 
                 [:button.tab-btn {:key label
                                   :class (when (= index tab-index)
                                            "active-line-top")
                                   :on-click #(on-index-change tab-index)} 
                  label])
               tabs))

(defn- render [{:keys [tabs index on-index-change] :as props}]
  (let [render (:render (tabs index))]
    [:div.tabbed-container
     
     [:div.content
      [render]]
     
     [:div.tabs
      (render-tabs props)]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main Method
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn tabbed-container [props]
  (render props))
