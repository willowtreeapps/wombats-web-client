(ns wombats-web-client.panels.account
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.components.add-button :as add-wombat-button]
            [wombats-web-client.components.header :refer [header]]
            [wombats-web-client.components.modals.wombat-modal
             :refer [wombat-modal]]
            [wombats-web-client.components.cards.wombat :as wombat-card]
            [wombats-web-client.events.user :refer [sign-out-event]]))

;; User Account Panel

(defn open-add-wombat-modal []
  (fn []
    (re-frame/dispatch [:set-modal {:fn wombat-modal
                                    :show-overlay true}])))

(defn account []
  (let [my-wombats @(re-frame/subscribe [:my-wombats])]
    [:div.account-panel
     [:div.heading-bar
      [header "MY WOMBATS"]
      [:button.logout {:on-click #(sign-out-event)} "LOG OUT"]]
     [:div.wombats (map wombat-card/root my-wombats)]
     [add-wombat-button/root (open-add-wombat-modal) "add-wombat"]]))
