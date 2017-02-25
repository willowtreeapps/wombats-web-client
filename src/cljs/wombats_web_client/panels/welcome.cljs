(ns wombats-web-client.panels.welcome
	(:require [re-frame.core :as re-frame]
			  [wombats-web-client.constants.urls :refer [github-signin-url]]))
;; Welcome Panel

(defonce welcome-page-title "Learn Clojure Through Gaming")
(defonce welcome-message-1 "Wombats is a platform for new developers to learn Clojure while playing through a fun and addictive game")
(defonce welcome-message-2 "Sign in with your GitHub account")

(defn render-wombat-logo []
	[:img {:src "img/img-logo-horizontal.png"
		   :srcset "img/img-logo-horizontal@2x.png 2x, 
	                img/img-logo-horizontal@3x.png 3x"
		   :class "img_logo_horizontal"}])

(defn render-welcome-title []
	[:div.welcome-title-container [:div.welcome-title-content welcome-page-title]])

(defn render-welcome-message []
	[:div.welcome-message-container
	[:div.welcome-message-content welcome-message-1]
	[:div.welcome-message-content welcome-message-2]])

(defn welcome []
	[:div.welcome-panel 
	[render-welcome-title]
	[render-welcome-message]
	[:div.welcome-button-container 
		[:div.welcome-button 
				[:a.welcome-button-label {:href github-signin-url} "start playing now"]]]])