(ns wombats-web-client.routes
  (:require [goog.events]
            [re-frame.core :as re-frame]
            [secretary.core :as secretary :refer-macros [defroute]]
            [wombats-web-client.utils.auth :refer [get-current-user-id
                                                   user-is-coordinator?]])
  (:import [goog.history Html5History EventType]))

(defn get-token []
  (str js/window.location.pathname js/window.location.search))

(defn SaneTokenTransformer
  ;; https://gist.github.com/andrejewski/3887f205fd834eea1b506a908db76e38
  []
  (this-as this
    (.call Html5History.TokenTransformer this)
    (set! (.-createUrl this)
          #(+ %2 %1))
    (set! (.-retrieveToken this)
          #(subs (.-pathname %2) (count %1)))
    this))

(defn make-history []
  (doto (Html5History. nil (SaneTokenTransformer.))
    (.setPathPrefix (str js/window.location.protocol
                         "//"
                         js/window.location.host))
    (.setUseFragment false)))

(defn- check-for-access-token
  "Access Token was passed by the server. Add token to storage,
  sanitize the URL, and then load user."
  [{:keys [access-token login-error]}]

  (when access-token
    (re-frame/dispatch [:login-success access-token]))

  (when login-error
    (re-frame/dispatch [:login-error login-error])))

(defn handle-url-change [e]
  (when-not (.-isNavigation e)
    ;; let's scroll to the top to simulate a navigation
    (js/window.scrollTo 0 0))
  (secretary/dispatch! (get-token)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Define routes here
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn app-routes []

  (defroute "/" {:keys [query-params]}
    (re-frame/dispatch [:set-active-panel {:panel-id :view-games-panel
                                           :params query-params}]))

  (defroute "/games/:game-id" {:keys [game-id]}
    (re-frame/dispatch [:set-active-panel {:panel-id :game-play-panel
                                           :params {:game-id game-id}}]))

  (defroute "/config" []
    (if (user-is-coordinator?)
      (re-frame/dispatch
       [:set-active-panel
        {:panel-id :config-panel}])
      (re-frame/dispatch
       [:set-active-panel
        {:panel-id :page-not-found-panel}])))

  (defroute "/simulator" []
    (re-frame/dispatch [:set-active-panel {:panel-id :simulator-panel}]))

  (defroute "/welcome" {:keys [query-params]}
    (check-for-access-token query-params)
    (re-frame/dispatch [:set-active-panel {:panel-id :welcome-panel}]))

  (defroute "/account" []
    (re-frame/dispatch [:set-active-panel {:panel-id :account-panel}]))

  (defonce history (doto (make-history)
                     (goog.events/listen EventType.NAVIGATE
                                         ;; wrap in a fn to allow live reloading
                                         #(handle-url-change %))
                     (.setEnabled true))))

(defn nav! [token]
  (.setToken history token))
