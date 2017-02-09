(ns wombats-web-client.components.arena
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.utils.canvas :as canvas]))

(defn- draw-image
  [canvas-element url x y width height]
  (when-not (nil? url)
    (let [img (js/Image.)]
      (set! (.-onload img) (fn [evt]
                             (.requestAnimationFrame js/window (fn []
                               (canvas/draw-image canvas-element
                                                  img
                                                  x
                                                  y
                                                  width
                                                  height)))))
      (set! (.-src img) url))))

(defn- draw-background
  "This draws the background of a cell (only called for cells that need it)"
  [canvas-element x y width height]
  (draw-image canvas-element
              "images/arena_bg.png"
              x
              y
              width
              height))

(defn- draw-meta
  "Draws generic meta objects"
  [canvas-element contents meta x y width height]
  (doseq [{type :type
           orientation :orientation} meta]
    (case type
      :shot
      (draw-image canvas-element
                  (case orientation
                    :n "images/fire_shot/fire_shot_up.png"
                    :w "images/fire_shot/fire_shot_left.png"
                    :e "images/fire_shot/fire_shot_right.png"
                    :s "images/fire_shot/fire_shot_down.png")
                  x y width height)

      :smoke
      (draw-image canvas-element
                  "images/smoke.png"
                  x y width height)

      :explosion
      (draw-image canvas-element
                  "images/explosion.png"
                  x y width height)

      (js/console.log type))))

(defn- draw-wood-barrier
  [canvas-element contents meta x y width height]
  (let [{deterioration-level :deterioration-level} contents]
    (draw-image canvas-element
                (str "images/wood-barrier/woodwall_" 
                     (case deterioration-level
                       :high "3"
                       :medium "2"
                       "1")
                     ".png")
                x y width height)))

(defn- draw-steel-barrier
  [canvas-element contents meta x y width height]
  (let [{deterioration-level :deterioration-level} contents]
    (draw-image canvas-element
                (str "images/steel-barrier/wall"
                     (case deterioration-level
                       :high "3"
                       :medium "2"
                       "")
                     ".png")
                x y width height)))

(defn- draw-food
  [canvas-element contents meta x y width height]
  (draw-image canvas-element
              "images/food/food_cherry.png"
              x y width height)
  (draw-meta canvas-element contents meta x y width height))

(defn- draw-poison
  [canvas-element contents meta x y width height]
  (draw-image canvas-element
              "images/poison/poison_vial.png"
              x y width height)
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
                    :w "left"
                    :e "right")]

    ;; Always draw the base zakano
    (draw-image canvas-element
                (str "images/zakano/zakano_" direction ".png")
                x
                y
                width
                height)

    ;; See if we need to add any meta to the zakano
    (doseq [{type :type} meta]
      
      (case type
        :shot
        (draw-image canvas-element
                    (str "images/zakano/zakano_" direction "_fire.png")
                    x y width height)

        :explosion
        (draw-image canvas-element
                    "images/explosion.png"
                    x y width height)

        :smoke
        (draw-image canvas-element
                    "images/smoke.png"
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
                    :w "left"
                    :e "right")]
    
    ;; Always draw the base wombat
    (draw-image canvas-element
                (str "images/wombats/wombat_" color "_" direction ".png")
                x
                y
                width
                height)

    ;; See if we need to add any meta to the wombat
    (doseq [{type :type} meta]

      (case type
        :shot
        (draw-image canvas-element
                    (str "images/wombats/wombat_" color "_" direction "_fire.png")
                    x
                    y
                    width
                    height)

        :explosion
        (draw-image canvas-element
                    "images/explosion.png"
                    x y width height)

        :smoke
        (draw-image canvas-element
                    "images/smoke.png"
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
