(ns wombats-web-client.utils.functions)

(defn in? [coll element]
  (some #(= element %) coll))

(defn no-blanks?
  ;; if something returned, a field is missing a value.
  [list]
  (empty? (filter nil? list)))
