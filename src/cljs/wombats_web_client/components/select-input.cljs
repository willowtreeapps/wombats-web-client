(ns wombats-web-client.components.select-input
  (:require [reagent.core :as reagent]
            [wombats-web-client.utils.errors :refer [required-field-error]]
            [wombats-web-client.components.inline-error :refer [inline-error]]))

(defn option-on-click [form-state form-key cmpnt-state option]
  (let [{:keys [id display-name]} option]
    ;; update form state with select value
    (swap! form-state assoc form-key id)
    ;; update component state view
    (swap! cmpnt-state assoc :selected-option display-name 
                             :is-closed true)))

(defn render-option [option form-key form-state cmpnt-state]
  (let [key (:id option)
        name (:display-name option)]
    [:li.option {:key key
                 :on-click #(option-on-click form-state form-key cmpnt-state option)}
     name]))

(defn select-input-on-click [cmpnt-state]
  (let [new-closed-state (not (:is-closed @cmpnt-state))]
    (swap! cmpnt-state assoc :is-closed new-closed-state)))

(defn select-input-on-blur [cmpnt-state form-state error-key]
  (swap! cmpnt-state assoc :is-closed true)

  (when (nil? (:selected-option @cmpnt-state)) 
    (swap! form-state assoc error-key required-field-error)))

(defn select-input-on-focus [form-state error-key]
  (swap! form-state assoc error-key nil))

(defn select-input 
  [params]
  (let [cmpnt-state (reagent/atom {:is-closed true
                                   :selected-option nil})]
    (fn [params]
      (let [{:keys [form-state 
                    form-key
                    error-key
                    option-list
                    label]} params
            closed (:is-closed @cmpnt-state)
            selected-option (:selected-option @cmpnt-state)
            is-no-selection? (nil? selected-option)
            displayed-option (if is-no-selection? label selected-option)
            error-val (get @form-state error-key)]
        [:div.select-input
         [:label.label.select-input-label label]

         ;; wraps all dropdown functionality
         [:div.dropdown {:class (when error-val "error")
                         :tab-index 0
                         :on-focus #(select-input-on-focus form-state error-key)
                         :on-blur #(select-input-on-blur cmpnt-state form-state error-key)}
          [:ul.option-list
           ;; will display default text or selection display name
           [:p.display-selected {:class (when is-no-selection? "default")
                                 :on-click #(select-input-on-click cmpnt-state)}
            displayed-option]

           ;; when dropdown is open, show options.
           (when-not closed 
             (for [option option-list] 
               ^{:key (:id option)} [render-option option form-key form-state cmpnt-state]))]
          ;; arrow toggles on openness of dropdown
          [:img.icon-arrow {:class (when-not closed "open-dropdown")
                            :src "/images/icon-arrow.svg"}]]
         [inline-error error-val]]))))

