(ns wombats-web-client.utils.functions)

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
