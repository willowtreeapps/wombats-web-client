(ns wombats-web-client.utils.canvas
  "Used to interact with a canvas element")

(defn- context
  "Gets the 2d context of a canvas"
  [canvas]
  (.getContext canvas "2d"))

(defn clear
  "Clears the contents of a canvas"
  ([canvas]
    (clear canvas 0 0 canvas.width canvas.height))
  ([canvas x y width height]
    (.clearRect (context canvas) x y width height)))

(defn draw-image
  "Draws an image at a certain coordinate
  w/ source/destination attributes"
  ([canvas image sx sy swidth sheight dx dy dwidth dheight]
    (.drawImage (context canvas)
                image
                sx sy swidth sheight
                dx dy dwidth dheight))

  ([canvas image sx sy swidth sheight dx dy dwidth dheight degreeRotation]
    (if (= degreeRotation 0)
      (draw-image canvas image sx sy swidth sheight dx dy dwidth dheight)

      (let [rotationRadians (/ (* js/Math.PI degreeRotation) 180)
            ctx (context canvas)]

        (.save ctx)
        (.translate ctx dx dy)
        (.translate ctx (/ dwidth 2) (/ dheight 2))
        (.rotate ctx rotationRadians)

        (.drawImage ctx
                    image
                    sx sy swidth sheight
                    (/ dwidth -2) (/ dheight -2) dwidth dheight)

        (.restore ctx)))))

(defn width
  "Gets the width of a canvas element"
  [canvas]
  (aget canvas "width"))

(defn height
  "Gets the height of a canvas element"
  [canvas]
  (aget canvas "height"))
