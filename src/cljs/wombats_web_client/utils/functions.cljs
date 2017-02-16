(ns wombats-web-client.utils.functions)

(defn in? [coll element]
  (some #(= element %) coll))
