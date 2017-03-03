(ns wombats-web-client.components.simulator.stack-trace
  (:require [re-frame.core :as re-frame]))

(defn- render!
  [{message :message
    stack-trace :stackTrace}]

  (if stack-trace
    [:div.stack-trace
     [:p.stack-trace-message message]
     [:ul.stack-trace-details
      (for [line-item stack-trace]
        ^{:key line-item} [:li.line-item line-item])]]
    [:p.no-stack-trace-message "No errors to report. Happy Coding."]))

(defn render
  []
  (let [stack-trace (re-frame/subscribe [:simulator/player-stack-trace])]
    (render! @stack-trace)))
