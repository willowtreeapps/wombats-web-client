(ns wombats-web-client.components.countdown-timer
  (:require [cljs-time.core :as t]
            [cljs-time.format :as f]
            [reagent.core :as reagent]))

(defn- seconds-until
  [time]
  (t/in-seconds (t/interval (t/now) time)))

(defn- format-time
  [time]
  (let [total-seconds (seconds-until time) 
        seconds (mod total-seconds 60)
        minutes (/ (- total-seconds seconds) 60)
        seconds-display (if (< seconds 10) (str "0" seconds) seconds)]
    (str minutes ":" seconds-display)))

(defn countdown-timer
  [start-time]

  (let [cmpnt-state (reagent/atom {:interval-fn nil})]
    (reagent/create-class
     {:component-will-mount
      (fn []
        ;; Force timer to redraw every second
        (swap! cmpnt-state 
               assoc 
               :interval-fn
               (.setInterval js/window
                             #(reagent/force-update-all)
                             1000)))

      :component-will-unmount
      (fn []
        (.clearInterval js/window 
                        (:interval-fn @cmpnt-state)))

      :reagent-render
      (fn []
        [:span {:class-name "countdown-timer"}
         (format-time start-time)])})))
