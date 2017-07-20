(ns wombats-web-client.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]
            [wombats-web-client.utils.games :as games]))

(re-frame/reg-sub
 :active-panel
 (fn [db _]
   (:active-panel db)))

(re-frame/reg-sub
 :query-params
 (fn [db _]
   (get-in db [:active-panel :params] {})))

(re-frame/reg-sub
 :auth-token
 (fn [db _]
   (:auth-token db)))

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
 :my-repositories
 (fn [db _]
   (:my-repositories db)))

(re-frame/reg-sub
 :my-wombats
 (fn [db _]
   (:my-wombats db)))

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
 :game/messages
 (fn [db _]
   (:game/messages db)))

(re-frame/reg-sub
 :game/details
 (fn [db [_ game-id]]
   (first (get-in db [:games game-id]))))

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

(re-frame/reg-sub
 :simulator/code
 (fn [db _]
   (get-in (games/get-player db) [:state :code :code])))

(re-frame/reg-sub
 :simulator/code-mode
 (fn [db _]
   (let [path (get-in (games/get-player db) [:state :code :path])]
     (when path
       (get {"clj" "clojure"
             "js" "javascript"
             "py" "python"}
            (last (clojure.string/split path #"\.")))))))

(re-frame/reg-sub
 :simulator/player-command
 (fn [db _]
   (get-in (games/get-player db) [:state :command])))

(re-frame/reg-sub
 :simulator/player-state
 (fn [db _]
   (get-in (games/get-player db) [:state :saved-state])))

(re-frame/reg-sub
 :simulator/player-stack-trace
 (fn [db _]
   (get-in (games/get-player db) [:state :error])))

(re-frame/reg-sub
 :simulator/active-frame
 (fn [db _]
   (get-in db [:simulator/state :game/frame :frame/arena])))

(re-frame/reg-sub
 :simulator/mini-map
 (fn [db _]
   (get-in (games/get-player db) [:state :mini-map])))

(re-frame/reg-sub
 :simulator/display-mini-map
 (fn [db _]
   (:simulator/mini-map db)))

(re-frame/reg-sub
 :simulator/active-pane
 (fn [db _]
   (:simulator/active-pane db)))

(re-frame/reg-sub
 :simulator/wombat-id
 (fn [db _]
   (:simulator/wombat-id db)))

(re-frame/reg-sub
 :simulator/template-id
 (fn [db _]
   (:simulator/template-id db)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Games subs
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(re-frame/reg-sub
 :games/games
 (fn [db _]
   (:games db)))

(re-frame/reg-sub
 :games/game-by-id
 (fn [db [_ game-id]]
   (get-in db [:games game-id])))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Bootstrapping subs
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(re-frame/reg-sub
 :bootstrapping
 (fn [db _]
   (:bootstrapping db)))

(re-frame/reg-sub
 :login-error
 (fn [db _]
   (:login-error db)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Arena  subs
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(re-frame/reg-sub
 :arenas
 (fn [db _]
   (:arenas db)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Access Key  subs
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(re-frame/reg-sub
 :access-keys
 (fn [db _]
   (:access-keys db)))
