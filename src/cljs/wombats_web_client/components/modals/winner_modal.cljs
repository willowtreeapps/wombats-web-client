(ns wombats-web-client.components.modals.winner-modal
  (:require [re-frame.core :as re-frame]))

(defn wombat-item [player]
  (let [{:keys [:player/user
                :player/wombat]} player
        username (:user/github-username user)
        wombat-name (:wombat/name wombat)]
    [:div.wombat-desc {:key username}
     [:div.wombat-name wombat-name]
     [:div.player-username username]]))

(defn winner-info [player]
  (let [{:keys [:player/color]} player]
    [:div.winner-info
     [:img.wombat-img {:src (str "/images/wombat_" color "_right.png")}]
     [wombat-item player]]))

(defn tied-info [wombats]
  [:div.winner-info
   (map wombat-item wombats)])

(defn winner-modal [players]
  (let [tied (< 1 (count players))
        title (if tied "WINNERS!" "WINNER!")]
    [:div {:class "modal winner-modal"}
     [:div.title title]
     [:div.modal-content
      (if tied [tied-info players] [winner-info (first players)])]
     [:div.redirect-buttons
      [:div.return-to-lobby [:a {:href "/"
                                 :on-click #(re-frame/dispatch
                                             [:set-modal nil])}
                             "RETURN TO LOBBY"]]]]))
