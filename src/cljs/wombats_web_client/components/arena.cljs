(ns wombats-web-client.components.arena
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.utils.canvas :as canvas]))

(defonce spritesheet-png "/images/spritesheet.png")
(defonce frame-time 1000)

(defn subscribe-to-spritesheet
  [img-name callback]
  (let [spritesheet (re-frame/subscribe [:spritesheet])
        sprite-info (get @spritesheet (keyword img-name))
        frame (:frame sprite-info)]
    (when frame
      (let [img (js/Image.)]
        (set! (.-src img) spritesheet-png)
        (.requestAnimationFrame js/window
                                (fn []
                                  (callback img frame)))))))

(defn- draw-image
  [canvas-element img-name x y width height]
  (subscribe-to-spritesheet img-name
                            (fn [img frame]
                              (canvas/draw-image canvas-element
                                                 img
                                                 (:x frame)
                                                 (:y frame)
                                                 (:w frame)
                                                 (:h frame)
                                                 x
                                                 y
                                                 width
                                                 height))))

(defn- draw-image-rotated
  [canvas-element img-name x y width height rotation]
  (subscribe-to-spritesheet img-name
                            (fn [img frame]
                              (canvas/draw-image-rotated
                               canvas-element
                               img
                               (:x frame)
                               (:y frame)
                               (:w frame)
                               (:h frame)
                               x y
                               width
                               height
                               rotation))))

(defn- draw-image-flipped-horizontally
  [canvas-element img-name x y width height]
  (subscribe-to-spritesheet img-name
                            (fn [img frame]
                              (canvas/draw-image-flipped-horizontally
                               canvas-element
                               img
                               (:x frame)
                               (:y frame)
                               (:w frame)
                               (:h frame)
                               x y
                               width
                               height))))

(defn- draw-background
  "This draws the background of a cell (only called for cells that need it)"
  [canvas-element x y width height]
  (draw-image canvas-element
              "arena_bg.png"
              x y width height))

(defn- meta-value
  "Gets a score for a meta type for sorting draw level"
  [meta]
  (case (:type meta)
    :shot      1
    :explosion 2
    :smoke     3
    0))

(defn- sort-meta
  "This sorts meta according to draw order"
  [meta]
  (sort #(let [val1 (meta-value %1)
               val2 (meta-value %2)]
           (cond
            (< val1 val2) -1
            (> val1 val2) 1
            :else 0)) meta))

(defn- draw-meta
  "Draws generic meta objects"
  [canvas-element contents meta x y width height]
  (doseq [{type :type
           orientation :orientation} (sort-meta meta)]
    (case type
      :shot
      (draw-image-rotated canvas-element
                          "fire_shot_right.png"
                          x y width height
                          (case orientation
                            :n 270
                            :w 180
                            :s 90
                            0))

      :smoke
      (draw-image canvas-element
                  "smoke.png"
                  x y width height)

      :explosion
      (draw-image canvas-element
                  "explosion.png"
                  x y width height)

      (js/console.log type))))

(defn- draw-wood-barrier
  [canvas-element contents meta x y width height]
  (let [{deterioration-level :deterioration-level} contents]
    (draw-image canvas-element
                (str "woodwall_"
                     (case deterioration-level
                       :high "3"
                       :medium "2"
                       "1")
                     ".png")
                x y width height)
    (draw-meta canvas-element contents meta x y width height)))

(defn- draw-steel-barrier
  [canvas-element contents meta x y width height]
  (let [{deterioration-level :deterioration-level} contents]
    (draw-image canvas-element
                (str "steelwall_"
                     (case deterioration-level
                       :high "3"
                       :medium "2"
                       "1")
                     ".png")
                x y width height)
    (draw-meta canvas-element contents meta x y width height)))

(defn- draw-food
  [canvas-element contents meta x y width height]
  (draw-image canvas-element
              "food_cherry.png"
              x y width height)
  (draw-meta canvas-element contents meta x y width height))

(defn- draw-poison
  [canvas-element contents meta x y width height]
  (draw-image canvas-element
              "poison_vial_2.png"
              x y width height)
  (draw-meta canvas-element contents meta x y width height))

(defn- draw-open
  "Draws whatever belongs on an open cell"
  [canvas-element contents meta x y width height]
  (draw-meta canvas-element contents meta x y width height))

(defn- draw-fog
  [canvas-element contents meta x y width height]
  (draw-image canvas-element
              ;; TODO Get official image for fog. issue #211
              "fog.png"
              x y width height)
  (draw-meta canvas-element contents meta x y width height))

(defn- draw-zakano
  [canvas-element contents meta x y width height]
  (let [{orientation :orientation} contents
        direction (case orientation
                    :s "front"
                    :n "back"
                    "right")
        img-name (str "zakano_" direction ".png")
        img-name-fire (str "zakano_" direction "_fire.png")
        flipped (= orientation :w)]

    ;; Always draw the base zakano
    (if flipped
      (draw-image-flipped-horizontally canvas-element
                                       img-name
                                       x y width height)
      (draw-image canvas-element
                  img-name
                  x y width height))

    ;; See if we need to add any meta to the zakano
    (doseq [{type :type} (sort-meta meta)]

      (case type
        :shot
        (if flipped
          (draw-image-flipped-horizontally canvas-element
                                           img-name-fire
                                           x y width height)
          (draw-image canvas-element
                      img-name-fire
                      x y width height))

        :explosion
        (draw-image canvas-element
                    "explosion.png"
                    x y width height)

        :smoke
        (draw-image canvas-element
                    "smoke.png"
                    x y width height)

        (js/console.log type)))))

(defn- draw-wombat
  [canvas-element contents meta x y width height]
  (let [{color :color
         orientation :orientation
         hp :hp} contents
         direction (case orientation
                     :s "front"
                     :n "back"
                     "right")
         img-name (str "wombat_" color "_" direction ".png")
         img-name-fire (str "wombat_" color "_" direction "_fire.png")
         flipped (= orientation :w)]

    ;; Always draw the base wombat
    (if flipped
      (draw-image-flipped-horizontally canvas-element
                                       img-name
                                       x y width height)
      (draw-image canvas-element
                  img-name
                  x y width height))

    ;; See if we need to add any meta to the wombat
    (doseq [{type :type} (sort-meta meta)]

      (case type
        :shot
        (if flipped
          (draw-image-flipped-horizontally canvas-element
                                           img-name-fire
                                           x y width height)
          (draw-image canvas-element
                      img-name-fire
                      x y width height))

        :explosion
        (draw-image canvas-element
                    "explosion.png"
                    x y width height)

        :smoke
        (draw-image canvas-element
                    "smoke.png"
                    x y width height)

        (js/console.log type)))))

(defn- draw-cell
  "Draw an arena cell on the canvas"
  [cell x y width height canvas-element]

  ;; Draw background first
  (draw-background canvas-element x y width height)

  (let [{contents :contents
         meta :meta} cell
         cell-type (:type contents)]

    (case cell-type

      :wood-barrier
      (draw-wood-barrier canvas-element contents meta x y width height)

      :steel-barrier
      (draw-steel-barrier canvas-element contents meta x y width height)

      :food
      (draw-food canvas-element contents meta x y width height)

      :poison
      (draw-poison canvas-element contents meta x y width height)

      :zakano
      (draw-zakano canvas-element contents meta x y width height)

      :wombat
      (draw-wombat canvas-element contents meta x y width height)

      :open
      (draw-open canvas-element contents meta x y width height)

      :fog
      (draw-fog canvas-element contents meta x y width height)

      (js/console.log "Unhandled: " cell-type))))

(defn- draw-cell-animate
  "Draw arena cells given the x and y of the previous and the next frame"
  [cell x-prev x-next])

(defn- draw-arena-canvas
  "Given a canvas element and the arena, draw the canvas"
  [{:keys [arena
           canvas-element]}]
  ;; Calculate the width and height of each cell
  (let [height (/ (canvas/height canvas-element) (count arena))
        width  (/ (canvas/width  canvas-element) (count (get arena 0)))]

    ;; Iterate through all of the arena rows
    (doseq [[y row] (map-indexed vector arena)]
      (doseq [[x cell] (map-indexed vector row)]
        (let
            [x-coord (* x width)
             y-coord (* y height)]

          (draw-cell cell
                     x-coord
                     y-coord
                     width
                     height
                     canvas-element))))))

(defn in?
  "Return true if coll contains elem"
  [elem coll]
  (some #(= elem %) coll))

(defn arena
  "Renders the arena on a canvas element, and subscribes to arena updates"
  [arena canvas-id]
  (let [canvas-element (.getElementById js/document canvas-id)]
    (when-not (nil? canvas-element)

      (draw-arena-canvas {:arena arena
                          :canvas-element canvas-element}))))

(defn add-locs
  "Add local :x and :y coordinates to arena matrix"
  [arena]
  (map-indexed
    (fn [y row] (map-indexed
                  (fn [x tile] (assoc tile :x x :y y))
                  row))
    arena))

(defn filter-arena
  "Filter the arena to return only nodes that contain one of the supplied types"
  ([arena] (flatten arena))
  ([arena filters]
  (let [node-list (flatten arena)]
    (filter #(in? (get-in % [:contents :type]) filters) node-list))))

(defn flatten-item
  [item]
  {:x (get item :x)
   :y (get item :y)
   :type (get-in item [:contents :type])})

(defn arena-history
  "Renders the arena on a canvas element, given the frames item and an index,
  allowing for animation between the frames"
  [{:keys [frames-vec frames-idx view-mode canvas-id]}]
  (let [arena (get-in @frames-vec
                      [@frames-idx
                       :game/frame
                       :frame/arena])
        ;; Get the coordinates of the wombats on the grid, reduce them into maps with
        ;; just the x, y, and type associated
        new-coords (println (map flatten-item (filter-arena (add-locs arena) [:wombat])))
        prev-frame (get-in @frames-vec
                           [(dec @frames-idx)
                            :game/frame
                            :frame/arena])
        prev-coords (println (map flatten-item (filter-arena (add-locs prev-frame) [:wombat])))
        canvas-element (.getElementById js/document canvas-id)]
    (when-not (nil? canvas-element)
      (draw-arena-canvas {:arena arena
                          :canvas-element canvas-element}))))
