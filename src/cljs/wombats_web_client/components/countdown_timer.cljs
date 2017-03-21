(ns wombats-web-client.components.countdown-timer
  (:require [wombats-web-client.utils.time :as t]
            [reagent.core :as reagent]))

(defn- time-left?
  [start-time]
  (pos? (t/seconds-until start-time)))

(defn- pad-time [time]
  (if (< time 10)
    (str "0" time)
    time))

(defn- total-time [smaller-unit divisor]
  (int (/ smaller-unit divisor)))

(defn- remaining-time [total larger-total multiplier]
  (- total (* larger-total multiplier)))

(defn- format-time [time]
 (if (time-left? time)
   (let [total-seconds (t/seconds-until time)
         total-minutes (total-time total-seconds 60)
         total-hours (total-time total-minutes 60)
         total-days (total-time total-hours 24)
         remaining-secs
         (remaining-time total-seconds total-minutes 60)
         remaining-mins
         (remaining-time total-minutes total-hours 60)
         remaining-hrs
         (remaining-time total-hours total-days 24)]
     (str
      (when (pos? total-days)
        (str (pad-time total-days) ":"))
      (when (pos? total-hours)
        (str (pad-time remaining-hrs) ":"))
      (pad-time remaining-mins) ":"
      (pad-time remaining-secs)))
   "00:00"))

(defn countdown-timer
  [start-time]

  (let [cmpnt-state (reagent/atom {:update nil})]
    (reagent/create-class
     {:reagent-render
      (fn [start-time]
        ;; Force timer to redraw every second
        (when (time-left? start-time)
          (.setTimeout js/window
                       #(swap! cmpnt-state update-in [:update] not)
                       1000))

        ;; triggers a rerender for active timers
        ;; will not affect timers with no interval timer
        (:update @cmpnt-state)

        [:span {:class-name "countdown-timer"}
         (format-time start-time)])})))
