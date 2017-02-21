(ns wombats-web-client.components.countdown-timer
  (:require [cljs-time.core :as t]
            [reagent.core :as reagent]))

(defn- seconds-until
  [time]
  (if (t/after? (t/now) time)
    0
    (t/in-seconds (t/interval (t/now) time))))

(defn- millis-until
  [time]
  (if (t/after? (t/now) time)
    0
    (mod (t/in-millis (t/interval (t/now) time))
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

  (let [cmpnt-state (reagent/atom {:interval-fn nil})]
    (reagent/create-class
     {:component-will-mount
      (fn []
        ;; Force timer to redraw every second, start interval when the countdown timer should switch
        (swap! cmpnt-state 
               assoc 
               :interval-fn
               (.setInterval js/window
                             #(reagent/force-update-all)
                             1000))

        #_(millis-until start-time))

      :component-will-unmount
      (fn []
        (.clearInterval js/window 
                        (:interval-fn @cmpnt-state)))

      :reagent-render
      (fn []
        [:span {:class-name "countdown-timer"}
         (format-time start-time)])})))
