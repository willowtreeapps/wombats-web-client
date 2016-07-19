(ns wombats_web_client.utils)

(defn in?
  "true if coll contains elm"
  [coll elm]
  (some #(= elm %) coll))
