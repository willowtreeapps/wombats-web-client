(ns wombats-web-client.utils.canvas
  "Used to interact with a canvas element")

(defn- context
  "Gets the 2d context of a canvas"
  [canvas]
  (.getContext canvas "2d"))

(defn clear
  "Clears the contents of a canvas"
  [canvas]
  (.clearRect (context canvas) 0 0 canvas.width canvas.height))

(defn draw-image
  "Draws an image at a certain coordinate"
  [canvas image x y width height]
  (.drawImage (context canvas) image x y width height))

(defn width
  "Gets the width of a canvas element"
  [canvas]
  (aget canvas "width"))

(defn height
  "Gets the height of a canvas element"
  [canvas]
  (aget canvas "height"))
