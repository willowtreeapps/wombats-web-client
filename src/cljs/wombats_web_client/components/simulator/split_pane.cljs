(ns wombats-web-client.components.simulator.split-pane
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [goog.events :as events])
  (:import [goog.events EventType]))

(defonce navbar-height 45)
(defonce max-height 107)

(defn- mouse-move-handler [{:keys [offset update top-size-px]}]
  (fn [evt]
    (let [y (- (.-clientY evt) (:y offset))]
      (if (> y (- js/innerHeight max-height))
        (reset! top-size-px (- js/innerHeight max-height))
        (reset! top-size-px y))
      (reset! update (not @update)))))

(defn- mouse-down-handler [e {:keys [update top-size-px]}]
  (let [offset             {:y (+ 0 navbar-height)}
        on-move            (mouse-move-handler {:offset offset
                                                :update update
                                                :top-size-px top-size-px})]
    (.preventDefault e)
    (events/listen js/window EventType.MOUSEMOVE
                   on-move)
    (events/listen js/window EventType.MOUSEUP
                   #(events/unlisten
                     js/window EventType.MOUSEMOVE on-move))))

(defn- render-divider [{:keys [divider-text update top-size-px]}]
  [:div.panel-divider
   {:on-mouse-down #(mouse-down-handler % {:update update
                                           :top-size-px top-size-px})}
   [:p.panel-divider-text @divider-text]
   [:hr.panel-grabber]])

(defn render
  ([top bottom update]
   (render top bottom update ""))
  ([top bottom update divider-text]
   (let [top-size-px (reagent/atom 145)]
     (reagent/create-class
      {:display-name "split-panel"
       :reagent-render (fn [top bottom update]
                         [:div.split-panel

                          ;; trigger rerender on resize
                          @update
                          [:div.panel-top
                           {:style {:height (str @top-size-px "px")}} top]
                          [render-divider {:divider-text divider-text
                                           :update update
                                           :top-size-px top-size-px}]
                          [:div.panel-bottom
                           bottom]])}))))
