(ns wombats-web-client.utils.time
  (:require [cljs-time.core :as t]
            [cljs-time.format :as tf]))

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

(def time-formatter (tf/formatter "MMM d, YYYY | h:mm A"))

(defn format-local-date [utc-date]
  (tf/unparse time-formatter
              (t/local-date-time utc-date)))

(defn local-time-to-utc [time]
  (let [date-split (clojure.string/split time #"-")
        year-num (js/parseInt (first date-split))
        month-num (js/parseInt (second date-split))
        time-string (clojure.string/split (last date-split) #"T")
        day-num (js/parseInt (first time-string))
        time-split (clojure.string/split (last time-string) #":")
        hour-num (js/parseInt (first time-split))
        min-num (js/parseInt (last time-split))
        local-time (t/local-date-time year-num
                                         month-num
                                         day-num
                                         hour-num
                                         min-num)
        utc-time-without-format (.toUTCIsoString
                                 (goog.date.UtcDateTime.fromTimestamp
                                  (.getTime local-time)) true)
        utc-split (clojure.string/split utc-time-without-format #" ")]
    (clojure.string/join "T" utc-split)))
