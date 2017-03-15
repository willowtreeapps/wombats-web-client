(ns wombats-web-client.components.simulator.configure
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [wombats-web-client.utils.forms :as f]
            [wombats-web-client.components.select-input :refer [select-input]]
            [wombats-web-client.utils.errors :refer [required-field-error]]))

(defn- update-simulator-configuration!
  [state]
  (let [wombat-id (:wombat-id @state)
        wombat-id-error (:wombat-id-error @state)
        template-id (:template-id @state)
        template-id-error (:template-id-error @state)]
    (when (nil? wombat-id)
      (swap! state assoc :wombat-id-error required-field-error))
    (when (nil? template-id)
      (swap! state assoc :template-id-error required-field-error))
    (when (and wombat-id template-id)
      (re-frame/dispatch [:simulator/initialize-simulator
                          {:simulator/template-id template-id
                           :simulator/wombat-id wombat-id}]))))

(defn render []
  (let [wombats (re-frame/subscribe [:my-wombats])
        sim-templates (re-frame/subscribe [:simulator/templates])
        selected-wombat (re-frame/subscribe [:simulator/wombat-id])
        selected-template (re-frame/subscribe [:simulator/template-id])
        form-state (reagent/atom {:wombat-id @selected-wombat
                                  :wombat-id-error nil
                                  :template-id @selected-template
                                  :template-id-error nil})]
    (fn []
      (let [wombats @wombats
            templates @sim-templates]
        [:div.configure
         [:p.pane-title "Configure Simulator"]
         [select-input {:form-state form-state
                        :form-key :template-id
                        :error-key :template-id-error
                        :option-list 
                        (f/optionize [:simulator-template/id] 
                                     [:simulator-template/arena-template :arena/name]
                                     templates)
                        :label "Select Template"}]
         [select-input {:form-state form-state
                        :form-key :wombat-id
                        :error-key :wombat-id-error
                        :option-list (f/optionize [:wombat/id] [:wombat/name] wombats)
                        :label "Select Wombat"}]
         [:button.update-btn {:on-click #(update-simulator-configuration! form-state)}
          "Update Simulator"]]))))
