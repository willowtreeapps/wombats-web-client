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

(defn- format-time
  [time]
  (let [seconds-left (seconds-until time)]
    (if (< seconds-left 0)
      "0:00"
      (let [seconds (mod seconds-left 60)
            seconds-formatted (if (< seconds 10)
                                (str "0" seconds)
                                seconds)
            minutes (/ (- seconds-left seconds) 60)]
        (str minutes ":" seconds-formatted)))))

(defn countdown-timer
  [start-time]

  (let [cmpnt-state (reagent/atom {:interval-fn nil
                                   :timeout-fn nil})
        swap-interval-fn! (fn []
                      (swap! cmpnt-state
                             assoc
                             :interval-fn
                             (.setInterval js/window
                                           #(reagent/force-update-all)
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
      (swap-timeout-fn!)

      :component-will-unmount
      (fn []
        (let [{:keys [interval-fn timeout-fn]} @cmpnt-state]
          (.clearTimeout js/window timeout-fn)
          (.clearInterval js/window interval-fn)))

      :reagent-render
      (fn []
        [:span {:class-name "countdown-timer"}
         (format-time start-time)])})))
