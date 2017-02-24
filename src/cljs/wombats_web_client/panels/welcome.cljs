(ns wombats-web-client.panels.welcome
	(:require [re-frame.core :as re-frame]
			  [wombats-web-client.components.welcome-button :as welcome-button]
			  [wombats-web-client.components.modals.join-wombat-modal :refer [join-wombat-modal]]

			  ))

(defn open-join-wombat-modal []
  (fn []
    (re-frame/dispatch [:set-modal {:fn join-wombat-modal
                                    :show-overlay? true}])))

(defn render-wombat-logo []
	[:img {:src "img/img-logo-horizontal.png"
     :srcset "img/img-logo-horizontal@2x.png 2x, 
              img/img-logo-horizontal@3x.png 3x"
     :class "img_logo_horizontal"}])

(defn render-welcome-title []
	[:div.welcome-title-container [:h1.welcome-title-content "Learn Clojure Through Game"]])

(defn render-welcome-message []

	[:div.welcome-message-container
	  [:div.welcome-message-content "Wombats is a platform for new 
		developers to learn Clojure while playing through a 
		fun and addictive game"]
	  [:div.welcome-message-content "Sign in with your GitHub account 
		to start playing."]])

(defn welcome []
	[:div.welcome-panel 
	 [render-welcome-title]
	 [render-welcome-message]
	 [:div.welcome-button-container [welcome-button/root (open-join-wombat-modal)]]
	]
)
