(ns wombats-web-client.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 :active-panel
 (fn [db _]
   (:active-panel db)))

(re-frame/reg-sub
 :current-user
 (fn [db _]
   (:current-user db)))

(re-frame/reg-sub
 :modal
 (fn [db _]
   (:modal db)))

(re-frame/reg-sub
 :my-wombats
 (fn [db _]
   (:my-wombats db)))

(re-frame/reg-sub
 :open-games
 (fn [db _]
   (:open-games db)))

(re-frame/reg-sub
 :my-games
 (fn [db _]
   (:my-games db)))

(re-frame/reg-sub
 :game/arena
 (fn [db _]
   (:game/arena db)))

(re-frame/reg-sub
 :game/messages
 (fn [db _]
   (:game/messages db)))
