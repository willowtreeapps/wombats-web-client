(ns wombats-web-client.components.chat-box
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent :refer [atom]]
            [wombats-web-client.utils.socket :as ws]
            [cljs-time.format :as f]
            [cljs-time.core :as t]))

(def message (atom ""))

(defn send-message
  [game-id]
  "Sends the message and clears the chat box"
  (fn []
    ;; Send message
    (ws/send-message :chat-message {:message @message
                                    :game-id game-id})

    ;; Clear message box
    (reset! message "")))

(defn check-for-enter
  [send-msg]
  "Sends a message if the user hit enter in the chat box"
  (fn [event]
    (let [key (-> event .-key)]
      (when (= "Enter" key)
        (send-msg)))
    event))

(defn format-time
  [timestamp]
  (f/unparse-local (f/formatter-local "h:mm A")
                   (f/parse-local timestamp)))

(defn display-messages
  [messages]
  (fn []

    [:ul {:class-name "chat-box-message-container"}
     (for [{:keys [username
                   message
                   timestamp]} @messages]
       ^{:key (str username "-" timestamp)}
       [:li {:class-name "chat-msg"}
        [:span {:class-name "msg-timestamp"} (format-time timestamp)]
        [:span {:class-name "msg-username"} username]
        [:span {:class-name "msg-body"} message]])]))

(defn chat-box-input
  [game-id]
  (let [send-msg-fn (send-message game-id)]
    [:div {:class-name "chat-box-input-container"}
     [:input {:class-name "chat-input"
              :name "chat-input"
              :type "input"
              :placeholder "Type something..."
              :value @message
              :on-key-press (check-for-enter send-msg-fn)
              :on-change #(reset! message (-> % .-target .-value))}]
     [:button {:class-name "chat-send-btn"
               :on-click send-msg-fn} "Send"]]))

(defn chat-box
  [game-id messages]
  (fn []
    [:div {:class-name "chat-box"}
     [display-messages messages]
     [chat-box-input game-id]]))
