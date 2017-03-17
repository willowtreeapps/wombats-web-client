(ns wombats-web-client.components.table)

(defn create-header-item [idx title]
  ^{:key (str idx "-" title)}
  [:th.title-cell {:class idx} title])

(defn create-data-cell [idx item]
  ^{:key (str idx "-" item)}
  [:td.data-cell {:class idx} item])

(defn create-rows [row-data-obj get-items-fn]
  (let [data (get-items-fn row-data-obj)]
    ^{:key (first data)}
    [:tr.data-row
         (map-indexed create-data-cell data)]))

(defn table [headers data get-items-fn]
  [:table.table
   [:thead
    [:tr.header-row
     (map-indexed create-header-item headers)]]
   [:tbody
    (for [row-data-obj data]
      (create-rows row-data-obj get-items-fn))]])
