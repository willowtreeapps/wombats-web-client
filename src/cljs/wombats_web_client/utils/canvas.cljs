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
  ([canvas image x y width height] (draw-image canvas 
                                               image
                                               x
                                               y
                                               width
                                               height
                                               0))
  ([canvas image x y width height rotation]
   (let [rotationRadians (/ (* js/Math.PI rotation) 180)
         ctx (context canvas)]
     
     (.translate ctx x y)
     (.translate ctx (/ width 2) (/ height 2))
     (.rotate ctx rotationRadians)
     
     (.drawImage (context canvas) image (/ width -2) (/ height -2) width height)
     
     (.rotate ctx (* -1 rotationRadians))
     (.translate ctx (/ width -2) (/ height -2))
     (.translate ctx (* -1  x) (* -1 y)))))

(defn width
  "Gets the width of a canvas element"
  [canvas]
  (aget canvas "width"))

(defn height
  "Gets the height of a canvas element"
  [canvas]
  (aget canvas "height"))
