(ns wombats-web-client.components.arena
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.utils.canvas :as canvas]))

(defn- draw-image
  [canvas-element url x y width height]
  (let [img (js/Image.)]
    (set! (.-onload img) (fn [evt]
      (canvas/draw-image canvas-element evt.srcElement x y width height)))
    (set! (.-src img) url)))

(defn- draw-cell
  "Draw an arena cell on the canvas"
  [cell-type x y width height canvas-element]
  (case cell-type

    :wood-barrier
      (draw-image canvas-element "images/arena-wall3.png" x y width height)

    :steel-barrier
      (draw-image canvas-element "images/arena-wall.png" x y width height)

    :food
      (draw-image canvas-element "images/arena-food.png" x y width height)

    :poison
      (draw-image canvas-element "images/arena-poison.png" x y width height)

    :zakano
      (draw-image canvas-element "images/arena-zakano.png" x y width height)

    :wombat
      (draw-image canvas-element "images/arena-wombat.png" x y width height)

    :open
      nil

    (js/console.log "Unhandled: " cell)))

(defn arena
  "Renders the arena on a canvas element, and subscribes to arena updates"
  []
  (let [current-arena (re-frame/subscribe [:arena])]
    (fn []
      (let [canvas-element [:canvas]]
        ;; Make sure to clear anything that's on the canvas
        (canvas/clear canvas-element)

        ;; Calculate the width and height of each cell
        (def height (/ (canvas/height canvas-element) (count current-arena)))
        (def width  (/ (canvas/width  canvas-element) (count (get current-arena 0))))

        ;; Iterate through all of the arena rows
        (doseq [[y row] (map-indexed vector current-arena)]
          (doseq [[x cell] (map-indexed vector row)]

            (def x-coord (* x width))
            (def y-coord (* y height))

            (draw-cell (get cell :cell-type) x-coord y-coord width height canvas-element)

        canvas-element))))