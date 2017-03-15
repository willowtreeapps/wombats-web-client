(ns wombats-web-client.components.countdown-timer
  (:require [cljs-time.core :as t]
            [reagent.core :as reagent]))

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

(defn- seconds-until
  [time]
  (if (before-now? time)
    0
    (t/in-seconds (interval-from-now time))))

(defn- millis-until
  [time]
  (if (before-now? time)
    0
    (mod (t/in-millis (interval-from-now time))
         1000)))

(defn- time-left?
  [start-time]
  (pos? (seconds-until start-time)))

(defn- format-time
  [time cmpnt-state]
  (let [seconds-left (seconds-until time)]
    ;; if time is left and requires an active timer
    (if (time-left? time)
      ;; calculate format of time
      (let [seconds (mod seconds-left 60)
            seconds-formatted (if (< seconds 10)
                                (str "0" seconds)
                                seconds)
            minutes (/ (- seconds-left seconds) 60)
            hours (int (/ minutes 60))
            minutes-adjusted (- minutes (* hours 60))
            minutes-single-digit? (< minutes-adjusted 10)
            minutes-formatted (str (when minutes-single-digit? "0")
                                   minutes-adjusted)
            has-hours? (pos? hours)]
        (str (when has-hours?
               (str hours ":"))
             minutes-formatted ":" seconds-formatted))

      ;; set to 0 time
      "0:00")))

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
         (format-time start-time cmpnt-state)])})))
