(ns wombats-web-client.components.arena
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.utils.canvas :as canvas]))

(defn- draw-image
  [canvas-element img-name x y width height rotation]
  (let [spritesheet (re-frame/subscribe [:spritesheet])
        sprite-info (get @spritesheet (keyword img-name))
        frame (:frame sprite-info)]
    (when-not (nil? frame)
      (let [img (js/Image.)]
        (set! (.-src img) "/images/spritesheet.png")
        (.requestAnimationFrame js/window (fn []
                                            (canvas/draw-image canvas-element
                                                               img
                                                               (:x frame)
                                                               (:y frame)
                                                               (:w frame)
                                                               (:h frame)
                                                               x
                                                               y
                                                               width
                                                               height
                                                               rotation)))))))

(defn- draw-background
  "This draws the background of a cell (only called for cells that need it)"
  [canvas-element x y width height]
  (draw-image canvas-element
              "arena_bg.png"
              x y width height 0))

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
      (draw-image canvas-element
                  "fire_shot_right.png"
                  x y width height (case orientation
                    :n 270
                    :w 180
                    :s 90
                    0))

      :smoke
      (draw-image canvas-element
                  "smoke.png"
                  x y width height 0)

      :explosion
      (draw-image canvas-element
                  "explosion.png"
                  x y width height 0)

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
                x y width height 0)
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
                x y width height 0)
    (draw-meta canvas-element contents meta x y width height)))

(defn- draw-food
  [canvas-element contents meta x y width height]
  (draw-image canvas-element
              "food_cherry.png"
              x y width height 0)
  (draw-meta canvas-element contents meta x y width height))

(defn- draw-poison
  [canvas-element contents meta x y width height]
  (draw-image canvas-element
              "poison_vial_2.png"
              x y width height 0)
  (draw-meta canvas-element contents meta x y width height))

(defn- draw-open
  "Draws whatever belongs on an open cell"
  [canvas-element contents meta x y width height]
  (draw-meta canvas-element contents meta x y width height))

(defn- draw-zakano
  [canvas-element contents meta x y width height]
  (let [{orientation :orientation} contents
        direction (case orientation
                    :s "front"
                    :n "back"
                    "right")]

    ;; Always draw the base zakano
    (draw-image canvas-element
                (str "zakano_" direction ".png")
                x y width height (case orientation
                  :w 180
                  0))

    ;; See if we need to add any meta to the zakano
    (doseq [{type :type} (sort-meta meta)]

      (case type
        :shot
        (draw-image canvas-element
                    (str "zakano_" direction "_fire.png")
                    x y width height (case orientation
                      :w 180
                      0))

        :explosion
        (draw-image canvas-element
                    "explosion.png"
                    x y width height 0)

        :smoke
        (draw-image canvas-element
                    "smoke.png"
                    x y width height 0)

        (js/console.log type)))))

(defn- draw-wombat
  [canvas-element contents meta x y width height]
  (let [{color :color
         orientation :orientation
         hp :hp} contents
        direction (case orientation
                    :s "front"
                    :n "back"
                    "right")]

    ;; Always draw the base wombat
    (draw-image canvas-element
                (str "wombat_" color "_" direction ".png")
                x
                y
                width
                height
                (case orientation
                  :w 180
                  0))

    ;; See if we need to add any meta to the wombat
    (doseq [{type :type} (sort-meta meta)]

      (case type
        :shot
        (draw-image canvas-element
                    (str "wombat_" color "_" direction "_fire.png")
                    x
                    y
                    width
                    height
                    (case orientation
                      :w 180
                      0))

        :explosion
        (draw-image canvas-element
                    "explosion.png"
                    x y width height 0)

        :smoke
        (draw-image canvas-element
                    "smoke.png"
                    x y width height 0)

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

      (js/console.log "Unhandled: " cell-type))))

(defn arena
  "Renders the arena on a canvas element, and subscribes to arena updates"
  [arena canvas-id]
  (let [canvas-element (.getElementById js/document canvas-id)]
    (when-not (nil? canvas-element)
      ;; Calculate the width and height of each cell
      (def height (/ (canvas/height canvas-element) (count arena)))
      (def width  (/ (canvas/width  canvas-element) (count (get arena 0))))

      ;; Iterate through all of the arena rows
      (doseq [[y row] (map-indexed vector arena)]
        (doseq [[x cell] (map-indexed vector row)]

          (def x-coord (* x width))
          (def y-coord (* y height))

          (draw-cell cell
                     x-coord
                     y-coord
                     width
                     height
                     canvas-element))))))
