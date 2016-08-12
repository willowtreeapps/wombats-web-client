(ns wombats_web_client.panels.settings
  (:require [re-frame.core :as re-frame]
            [wombats_web_client.services.forms :as f]))

(defn is-valid? [form]
  (if (empty? (f/get-value :repo form))
    false
    true))

(defn remove-bot-action
  [{:keys [repo name]}]
  (re-frame/dispatch [:remove-bot repo]))

(defn render-bot-list
  [bots]
  [:ul.bot-list
   [:li.bot
    [:p.bot-name.header "Bot Name"]
    [:p.bot-repo.header "Repo Name"]]
   (for [bot bots]
     ^{:key (:repo bot)} [:li.bot
                          [:p.bot-name (:name bot)]
                          [:p.bot-repo (:repo bot)]
                          [:button.remove-bot-btn {:on-click #(remove-bot-action bot)} "Remove Bot"]])])

(defn settings-title []
   [:h1 "This is the Settings Page."])

(defn link-to-home-page []
  [:a {:href "#/"} "Go Home"])

(defn settings-panel []
  (let [user (re-frame/subscribe [:user])
        form (f/initialize {:repo ""
                            :name ""})]
    (fn []
      [:div.settings-panel
        (settings-title)
        [:h2 (:login @user)]
        [:img {:src (:avatar_url @user)
              :alt (str (:login @user) "'s avatar")
        :class-name "avatar"}]

        (render-bot-list (:bots @user))

        [:p.header "Settings"]

        ;; Add Bot
        [:p "Add Bot"]
        [:input {:type "text"
                :value (f/get-value :name form)
                :on-change #(f/set-value! :name (-> % .-target .-value) form)
                :placeholder "Bot Name"}]
        [:input {:type "text"
                :value (f/get-value :repo form)
                :on-change #(f/set-value! :repo (-> % .-target .-value) form)
                :placeholder "Repo Name"}]
        [:input {:type "button"
                :value "Add Bot"
                :on-click #(f/on-submit {:form form
                                         :validator is-valid?
                                         :dispatch [:add-bot (:doc @form) (:_id @user)]})}]
        (link-to-home-page)])))
