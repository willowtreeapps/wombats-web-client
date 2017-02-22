(ns wombats-web-client.components.modals.wombat-modal
  (:require [re-frame.core :as re-frame]))


(defn winner-modal [color name username]
;; color = color of winning wombat
;; name = name of winning wombat
;; username = github name of winner
  (fn []
    [:div {:class "modal winner-modal"}
     [:div.title "WINNER!"]
     [:img.wombat-img {:src (str "/images/wombat_" color "_right.png")}]
     [:div.wombat-name name]
     [:div.player-username username]
     [:div.redirect-buttons
      [:div.return-to-lobby [:a {:href "#/"
                                 :on-click #(re-frame/dispatch [:set-modal nil])} 
                             "RETURN TO LOBBY"]]]]))
