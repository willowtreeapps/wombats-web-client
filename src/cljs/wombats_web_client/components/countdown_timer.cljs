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
  (< 0 (seconds-until start-time)))

(defn- clear-timers [cmpnt-state]
  (let [{:keys [interval-fn timeout-fn]} @cmpnt-state]
    (.clearTimeout js/window timeout-fn)
    (.clearInterval js/window interval-fn))
  (swap! cmpnt-state assoc :update-time-counter 0))

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
            minutes-formatted (str (when (< minutes-adjusted 10) "0") minutes-adjusted)]
        (str (when (> hours 0) (str hours ":")) minutes-formatted ":" seconds-formatted))

      ;; set to 0 time and clear timer
      (do
        (clear-timers cmpnt-state)
        "0:00"))))

(defn countdown-timer
  [start-time id]

  (let [cmpnt-state (reagent/atom {:interval-fn nil
                                   :timeout-fn nil
                                   :update-time-counter 0})
        swap-interval-fn! (fn []
                            (swap! cmpnt-state
                                   assoc
                                   :interval-fn
                                   (.setInterval js/window
                                                 ;; update state to trigger rerender
                                                 #(swap! cmpnt-state update-in [:update-time-counter] inc)
                                                 1000)))
        swap-timeout-fn! (fn []
                           (swap! cmpnt-state
                                  assoc
                                  :timeout-fn
                                  (.setTimeout js/window
                                               swap-interval-fn!
                                               ;; Give the browser some extra time to render the next second
                                               (- (millis-until start-time) 100))))]
    (reagent/create-class
     {:component-will-mount
      ;; Force timer to redraw every second, start interval when the countdown timer should switch
      #(swap-timeout-fn!)

      :component-will-unmount #(clear-timers cmpnt-state)

      :reagent-render
      (fn []
        ;; triggers a rerender for active timers, will not effect timers with no interval timer
        (:update-counter @cmpnt-state)

        [:span {:class-name "countdown-timer"}
         (format-time start-time cmpnt-state)])})))
