(ns wombats-web-client.utils.functions
  (:require [wombats-web-client.constants.ui :refer [mobile-window-width]]))

(defn in? [coll element]
  (some #(= element %) coll))

(defn no-blanks?
  ;; if something returned, a field is missing a value.
  [list]
  (empty? (filter nil? list)))

(defn bind-value
  "bind a value between upper and lower bounds"
  [val lower upper]
  (cond
    (> val upper) upper
    (< val lower) lower
    :else val))

(defn- mobile-device?
  "Returns true if the client has a screen width less than mobile-window-width"
  []
  (let [width (.-innerWidth js/window)]
    (<= width mobile-window-width)))
