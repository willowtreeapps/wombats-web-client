(ns wombats-web-client.utils.forms
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.utils.errors :as e]))

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

(defn get-key-as-string
  [key-name]
  (if (namespace key-name)
    (str (namespace key-name) "/" (name key-name))
    (name key-name)))

(defn- get-test-fn
  [error]
  (condp = error
    e/required-field-error e/required-field-fn
    e/not-an-integer e/not-an-integer-fn
    e/min-five e/min-five-fn
    e/max-twenty-five e/max-twenty-five-fn
    "default" e/default-error-fn))

(defn input-error! [check cmpnt-state]
  (let [{:keys [key-name test-fn error]} check
        input (key-name @cmpnt-state)
        key-string (get-key-as-string key-name)
        error-key (keyword (str key-string "-error"))
        test-func (if (nil? test-fn)
                    (get-test-fn error)
                    test-fn)]
    (when (test-func input)
      (swap! cmpnt-state assoc error-key error))))
