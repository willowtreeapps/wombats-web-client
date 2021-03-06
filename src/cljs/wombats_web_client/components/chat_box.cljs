(ns wombats-web-client.components.chat-box
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent :refer [atom]]
            [wombats-web-client.utils.games :refer [get-player-by-username]]
            [wombats-web-client.utils.socket :as ws]
            [cljs-time.format :as f]
            [cljs-time.core :as t]
            [wombats-web-client.constants.colors :refer [colors-8]]))

(def message (atom ""))

(defn send-message-fn
  "Sends the message and clears the chat box"
  [game-id]
  (fn []
    ;; Send message
    (ws/send-message :chat-message {:message @message
                                    :game-id game-id})
    ;; Clear message box
    (reset! message "")))

(defn check-for-enter
  "Sends a message if the user hit enter in the chat box"
  [send-msg]
  (fn [event]
    (let [key (.-key event)]
      (when (= "Enter" key)
        (send-msg)))
    event))

(defn format-time
  [timestamp]
  (f/unparse (f/formatter "h:mm A")
             (t/to-default-time-zone timestamp)))

(defn get-username-color [players username]
  (let [player (get-player-by-username username players)
        color-text (:player/color player)
        colors-8-filter-fn (fn [color] (= (:color-text color) color-text))
        color-hex (:color-hex (first (filter colors-8-filter-fn colors-8)))]
    color-hex))

(defn default-message []
  [:li.chat-msg
   [:span.msg-body.default "Say something already!"]])

(defn display-messages
  [messages players]
  (let [messages @messages
        element (first
                 (array-seq
                  (.getElementsByClassName
                   js/document
                   "chat-box-message-container")))]

    ;; Check if you should auto scroll to the bottom
    (when (and element
               (=
                (+ element.scrollTop element.clientHeight)
                element.scrollHeight))
      ;; On the next tick, scroll the rendered element down
      (js/setTimeout #(set! (.-scrollTop element)
                            element.scrollHeight)
                     0))

    [:ul.chat-box-message-container
     (if (pos? (count messages))
       (for [[index {:keys [username
                            message
                            timestamp]}] (map-indexed vector messages)]

         ^{:key (str username "-" timestamp "-" index)}
         [:li.chat-msg
          [:span.msg-timestamp (format-time timestamp)]
          [:span.msg-username
           {:style
            {:color (get-username-color players username)}}
           username]
          [:span.msg-body message]])
       [default-message])]))

(defn chat-box-input
  [game-id]
  (let [send-msg-fn (send-message-fn game-id)]
    [:div.chat-box-input-container
     [:input.chat-input
      {:name "chat-input"
       :type "input"
       :placeholder "Type something..."
       :value @message
       :on-key-press (check-for-enter send-msg-fn)
       :on-change #(reset! message (-> % .-target .-value))}]
     [:button.chat-send-btn
      {:on-click send-msg-fn} "SEND"]]))

(defn chat-box
  [game messages]
  (let [{:keys [:game/id
                :game/players]} @game]
    [:div.chat-box
     [display-messages messages players]
     [chat-box-input id]]))
