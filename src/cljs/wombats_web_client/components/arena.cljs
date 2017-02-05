(ns wombats-web-client.components.arena
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.utils.canvas :as canvas]))

(defn- draw-image
  [canvas-element url x y width height]
  (let [img (js/Image.)]
    (set! (.-onload img) (fn [evt]
      (canvas/draw-image canvas-element evt.srcElement x y width height)))
    (set! (.-src img) url)))

(defn- get-wood-barrier
  [contents meta]
  (let [{hp :hp} contents]
    "images/wood-barrier/woodwall_1.png"))

(defn- get-steel-barrier
  [contents meta]
  "images/steel-barrier/wall.png")

(defn- get-food
  [contents meta]
  "images/food/food_cherry.png")

(defn- get-poison
  [contents meta]
  "images/poison/poison_vial.png")

(defn- get-zakano
  [contents meta]
  (let [{orientation :orientation} contents
        direction (case orientation
                    :s "front"
                    :n "back"
                    :w "left"
                    :e "right")]
    (str "images/zakano/zakano_" direction ".png")))

(defn- get-wombat
  [contents meta]
  (let [{color :color
         orientation :orientation
         hp :hp} contents
        direction (case orientation
                    :s "front"
                    :n "back"
                    :w "left"
                    :e "right")]
    (str "images/wombats/wombat_" color "_" direction ".png")))

(defn- draw-cell
  "Draw an arena cell on the canvas"
  [cell x y width height canvas-element]

  (let [{contents :contents
         meta :meta} cell
        cell-type (:type contents)]

    (case cell-type

      :wood-barrier
      (draw-image canvas-element
                  (get-wood-barrier contents meta)
                  x
                  y
                  width
                  height)

      :steel-barrier
      (draw-image canvas-element
                  (get-steel-barrier contents meta)
                  x
                  y
                  width
                  height)

      :food
      (draw-image canvas-element
                  (get-food contents meta)
                  x
                  y
                  width
                  height)

      :poison
      (draw-image canvas-element
                  (get-poison contents meta)
                  x
                  y
                  width
                  height)

      :zakano
      (draw-image canvas-element
                  (get-zakano contents meta)
                  x
                  y
                  width
                  height)

      :wombat
      (draw-image canvas-element
                  (get-wombat contents meta)
                  x
                  y
                  width
                  height)

      :open
      nil

      (js/console.log "Unhandled: " cell-type))))

(defn arena
  "Renders the arena on a canvas element, and subscribes to arena updates"
  [arena canvas-id]
  (let [canvas-element (.getElementById js/document canvas-id)]
    (when (not (nil? canvas-element))
      ;; Make sure to clear anything that's on the canvas (not sure if this is needed)
      (canvas/clear canvas-element)

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
