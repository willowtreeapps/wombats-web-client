(ns wombats-web-client.components.simulator.stack-trace
  (:require [re-frame.core :as re-frame]))

(defn render []
  (let [stack @(re-frame/subscribe [:simulator/player-stack-trace])
        {message :message
         stack-trace :stackTrace} stack]
    (if stack-trace
      [:div.stack-trace
       [:p.stack-trace-message message]
       [:ul.stack-trace-details
        (for [line-item stack-trace]
          ^{:key line-item} [:li.line-item line-item])]])))
