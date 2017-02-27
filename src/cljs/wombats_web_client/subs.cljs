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
 :modal-error
 (fn [db _]
   (:modal-error db)))

(re-frame/reg-sub
 :my-wombats
 (fn [db _]
   (:my-wombats db)))

(re-frame/reg-sub
 :open-games
 (fn [db _]
   (:open-games db)))

(re-frame/reg-sub
 :my-open-games
 (fn [db _]
   (:my-open-games db)))

(re-frame/reg-sub
 :closed-games
 (fn [db _]
   (:closed-games db)))

(re-frame/reg-sub
 :my-closed-games
 (fn [db _]
   (:my-closed-games db)))

(re-frame/reg-sub
 :joined-games
 (fn [db _]
   (:joined-games db)))

(re-frame/reg-sub
 :join-game-selections
 (fn [db _]
   (:join-game-selections db)))

(re-frame/reg-sub
 :game/arena
 (fn [db _]
   (:game/arena db)))

(re-frame/reg-sub
 :game/info
 (fn [db _]
   (:game/info db)))

(re-frame/reg-sub
 :game/messages
 (fn [db _]
   (:game/messages db)))

(re-frame/reg-sub
 :game/details
 (fn [db [_ game-id]]
   (first (filter #(= (:game/id %) game-id)
                  (:open-games db)))))

(re-frame/reg-sub
 :spritesheet
 (fn [db _]
   (:spritesheet db)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Simulator subs
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(re-frame/reg-sub
 :simulator/templates
 (fn [db _]
   (:simulator/templates db)))

(re-frame/reg-sub
 :simulator/state
 (fn [db _]
   (:simulator/state db)))
