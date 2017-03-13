(ns wombats-web-client.components.simulator.configure
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.utils.forms :as f]))

(defn- update-simulator-configuration!
  [state]
  (re-frame/dispatch [:simulator/initialize-simulator
                      {:simulator/template-id (:template-id state)
                       :simulator/wombat-id (:wombat-id state)}]))

(defn- update-form-state
  [e id form-state]
  (swap! form-state assoc id (f/get-value e)))

(defn- render-option [id name selected-id]
  ^{:key id}
  [:option {:value id
            :default-value (= id selected-id)} name])

(defn- render-template-select
  [templates form-state]
  (when-not (:template-id @form-state)
    (swap! form-state assoc :template-id (:simulator-template/id (first templates))))

  [:select.select {:on-change #(update-form-state % :template-id form-state)}
   (for [{id :simulator-template/id
          {arena-name :arena/name} :simulator-template/arena-template} templates]
     (render-option id arena-name (:template-id @form-state)))])

(defn- render-wombat-select
  [wombats form-state]
  (when-not (:wombat-id @form-state)
    (swap! form-state assoc :wombat-id (:wombat/id (first wombats))))

  [:select.select {:on-change #(update-form-state % :wombat-id form-state)}
   (for [{id :wombat/id
          wombat-name :wombat/name} wombats]
     (render-option id wombat-name (:wombat-id @form-state)))])

(defn- render-pane
  [wombats templates form-state]
  [:div.configure
   [:p.pane-title "Configure Simulator"]
   (render-template-select templates form-state)
   (render-wombat-select wombats form-state)
   [:button.update-btn {:on-click #(update-simulator-configuration! @form-state)}
    "Update Simulator"]])

(defn render []
  (let [wombats (re-frame/subscribe [:my-wombats])
        sim-templates (re-frame/subscribe [:simulator/templates])
        selected-wombat (re-frame/subscribe [:simulator/wombat-id])
        selected-template (re-frame/subscribe [:simulator/template-id])
        form-state (atom {:wombat-id @selected-wombat
                          :template-id @selected-template})]
    (render-pane @wombats @sim-templates form-state)))
