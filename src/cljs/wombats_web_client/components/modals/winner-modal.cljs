(ns wombats-web-client.components.modals.wombat-modal
  (:require [re-frame.core :as re-frame]))

(defn wombat-item [wombat]
  (let [{:keys [wombat-name username]} wombat]
    [:div.wombat-desc {:key username}
     [:div.wombat-name wombat-name]
     [:div.player-username username]]))

(defn winner-info [wombat]
  (let [{:keys [wombat-color wombat-name username]} wombat]
    [:div.winner-info
     [:img.wombat-img {:src (str "/images/wombat_" wombat-color "_right.png")}]
     [wombat-item wombat]]))

(defn tied-info [wombats]
  [:div.winner-info
   (map wombat-item wombats)])

(defn winner-modal [wombats]
  (let [tied? (< 1 (count wombats))
        title (if tied? "WINNERS!" "WINNER!")]
    [:div {:class "modal winner-modal"}
     [:div.title title]
     (if tied? [tied-info wombats] [winner-info (first wombats)])
     [:div.redirect-buttons
      [:div.return-to-lobby [:a {:href "/"
                                 :on-click (fn [evt]
                                             (re-frame/dispatch [:set-modal nil]))}
                             "RETURN TO LOBBY"]]]]))
