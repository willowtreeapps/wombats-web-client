(ns wombats-web-client.utils.forms
  (:require [re-frame.core :as re-frame]))

(defn get-value [element]
  (-> element .-target .-value))

(defn optionize [id-key name-key coll]
  (map (fn [el] {:id (get-in el id-key)
                 :display-name (get-in el name-key)}) coll))

(defn cancel-modal-input []
  [:input.modal-button {:type "button"
                        :value "CANCEL"
                        :on-click (fn []
                                    (re-frame/dispatch [:set-modal nil]))}])

(defn submit-modal-input [submit-value on-click-fn]
  [:input.modal-button {:type "button"
                        :value submit-value
                        :on-click on-click-fn}])
