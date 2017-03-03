(ns wombats-web-client.components.simulator.configure
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.utils.socket :as ws]))

(defn- update-simulator-configuration!
  [state]

  (ws/send-message
   :connect-to-simulator
   {:simulator-template-id (:template-id state)
    :wombat-id (:wombat-id state)})

  (re-frame/dispatch [:simulator/update-configuration state]))

(defn- update-form-state
  [e id form-state]
  (swap! form-state assoc id (-> e .-target .-value)))

(defn- render-template-select
  [templates form-state]
  (when-not (:template-id @form-state)
    (swap! form-state assoc :template-id (:simulator-template/id (first templates))))

  [:select.select {:name "template"
                   :on-change #(update-form-state % :template-id form-state)}
   (for [{id :simulator-template/id
          {arena-name :arena/name} :simulator-template/arena-template} templates]
     ^{:key id}
     [:option {:value id
               :default-value (= id (:template-id @form-state))}
      arena-name])])

(defn- render-wombat-select
  [wombats form-state]
  (when-not (:wombat-id @form-state)
    (swap! form-state assoc :wombat-id (:wombat/id (first wombats))))

  [:select.select {:name "wombat"
                   :on-change #(update-form-state % :wombat-id form-state)}
   (for [{id :wombat/id
          name :wombat/name} wombats]
     ^{:key id}
     [:option {:value id
               :default-value (= (:wombat/id id) (:wombat-id @form-state))}
      name])])

(defn- render!
  [wombats templates form-state]
  [:div.configure
   [:p.pane-title "Configure Simulator"]
   (render-template-select templates form-state)
   (render-wombat-select wombats form-state)
   [:button.update-btn {:on-click #(update-simulator-configuration! @form-state)}
    "Update Simulator"]])

(defn render
  []
  (let [wombats (re-frame/subscribe [:my-wombats])
        sim-templates (re-frame/subscribe [:simulator/templates])
        selected-wombat (re-frame/subscribe [:simulator/wombat-id])
        selected-template (re-frame/subscribe [:simulator/template-id])
        form-state (atom {:wombat-id @selected-wombat
                          :template-id @selected-template})]
    (render! @wombats @sim-templates form-state)))
