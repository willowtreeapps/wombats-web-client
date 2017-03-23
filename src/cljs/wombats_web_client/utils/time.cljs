(ns wombats-web-client.utils.time
  (:require [cljs-time.core :as t]))

(defn- before-now?
  "Returns a boolean whether time is in the past"
  [time]
  (or (nil? time)
      (t/after? (t/now)
                time)))

(defn- interval-from-now
  [time]
  (t/interval (t/now)
              time))

(defn seconds-until
  [time]
  (if (before-now? time)
    0
    (t/in-seconds (interval-from-now time))))
