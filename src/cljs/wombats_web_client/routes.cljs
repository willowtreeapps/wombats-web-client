(ns wombats-web-client.routes
    (:require-macros [secretary.core :refer [defroute]])
    (:require [clojure.string :refer [split]]
              [secretary.core :as secretary]
              [pushy.core :as pushy]
              [re-frame.core :as re-frame]
              [wombats-web-client.constants.urls :refer [github-signin-url]]
              [wombats-web-client.utils.auth :refer [get-current-user-id
                                                     user-is-coordinator?]]))

(defonce history (pushy/pushy secretary/dispatch!
                              #(when (secretary/locate-route
                                      (first (split % "?")))
                                 %)))

(defn- check-for-access-token
  "This checks query params for routes to do some
  bootstrapping stuff."
  [{:keys [access-key access-token login-error]}]

  ;; If there was an accesskey provided, we want to redirect
  ;; to the API signin endpoint providing the access-key
  (when access-key
    (-> js/window
        .-location
        (.replace (str github-signin-url "?access-key=" access-key))))

  ;; Access Token was passed by the server. Add token to storage,
  ;; sanitize the URL, and then load user.
  (when access-token
    (re-frame/dispatch-sync [:login-success access-token])
    (re-frame/dispatch [:redirect-authenticated]))

  (when login-error
    (re-frame/dispatch [:login-error login-error])))

(defn app-routes []
  ;; define routes here

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

  (pushy/start! history))
