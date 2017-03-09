(ns wombats-web-client.components.chat-box
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent :refer [atom]]
            [wombats-web-client.utils.socket :as ws]
            [cljs-time.format :as f]
            [cljs-time.core :as t]
            [wombats-web-client.constants.colors :refer [colors-8]]))

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
  (f/unparse (f/formatter "h:mm A")
             (t/to-default-time-zone timestamp)))

(defn get-username-color [stats username]
  (let [stat-filter-fn (fn [stat] (= (:username stat) username))
        color-text (:color (first (filter stat-filter-fn stats)))
        colors-8-filter-fn (fn [color] (= (:color-text color) color-text))
        color-hex (:color-hex (first (filter colors-8-filter-fn colors-8)))]
    color-hex))

(defn default-message []
  [:li {:class-name "chat-msg"}
   [:span {:class-name "msg-body default"} "Say something already!"]])

(defn display-messages
  [messages game]
  (let [stats (:game/stats game)
        messages @messages
        element (first (array-seq (.getElementsByClassName js/document
                                                           "chat-box-message-container")))]

    ;; Check if you should auto scroll to the bottom
    (when (and element (= (+ element.scrollTop element.clientHeight) element.scrollHeight))
      ;; On the next tick, scroll the rendered element down
      (js/setTimeout #(set! (.-scrollTop element)
                            element.scrollHeight)
                     0))

    [:ul {:class-name "chat-box-message-container"}
     (if (pos? (count messages))
       (for [{:keys [username
                     message
                     timestamp]} messages]
         ^{:key (str username "-" timestamp)}
         [:li {:class-name "chat-msg"}
          [:span {:class-name "msg-timestamp"} (format-time timestamp)]
          [:span {:class-name "msg-username"
                  :style {:color (get-username-color stats username)}} username]
          [:span {:class-name "msg-body"} message]])
       [default-message])]))

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
               :on-click send-msg-fn} "SEND"]]))



(defn chat-box
  [game-id messages stats]
  [:div {:class-name "chat-box"}
   [display-messages messages stats]
   [chat-box-input game-id]])
