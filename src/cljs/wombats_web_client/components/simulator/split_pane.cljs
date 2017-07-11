(ns wombats-web-client.components.simulator.split-pane
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [goog.events :as events])
  (:import [goog.events EventType]))


(defonce navbar-height 45)
(defonce max-height 105)
(defonce top-size-px (reagent/atom 145))

(defn- mouse-move-handler [offset update]
  (fn [evt]
    (let [y (- (.-clientY evt) (:y offset))]
      (if (> y (- js/innerHeight max-height))
        (reset! top-size-px (- js/innerHeight max-height))
        (reset! top-size-px y))
      (reset! update (not @update)))))


(defn- mouse-up-handler [on-move]
  (fn me [evt]
    (events/unlisten js/window EventType.MOUSEMOVE
                     on-move)))

(defn- mouse-down-handler [e update]
  (let [offset             {:y (+ 0 navbar-height)}
        on-move            (mouse-move-handler offset update)]
    (.preventDefault e)
    (events/listen js/window EventType.MOUSEMOVE
                   on-move)
    (events/listen js/window EventType.MOUSEUP
                   (mouse-up-handler on-move))))

(defn- render-divider [panel-change]
       [:div {:on-mouse-down #(mouse-down-handler % panel-change)}
        [:hr.panel-divider]
        [:hr.panel-grabber]])

(defn render [top bottom update]
  (reagent/create-class
   {:display-name "split-panel"
    :reagent-render (fn [top bottom update]
                      [:div.split-panel

                       ;; trigger rerender on resize
                       @update

                       [:div.panel-top
                        {:style {:height (str @top-size-px "px")}} top]
                       [render-divider update]
                       [:div.panel-bottom
                        bottom]])}))
