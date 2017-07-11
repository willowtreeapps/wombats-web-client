(ns wombats-web-client.components.simulator.stack-trace
  (:require [re-frame.core :as re-frame]))

(defn render [simulator-data]
  (let [{message :message
         stack-trace :stackTrace} (:player-stack-trace @simulator-data)]
    (if stack-trace
      [:div.stack-trace
       [:p.stack-trace-message message]
       [:ul.stack-trace-details
        (for [line-item stack-trace]
          ^{:key line-item} [:li.line-item line-item])]])))
