(ns wombats-web-client.db
  (:require [wombats-web-client.utils.local-storage :refer [get-token]]))

(def default-db
  {:active-panel nil
   :auth-token (get-token)
   :bootstrapping? true
   :current-user nil
   :users []
   :modal nil

   :game/arena nil
   :game/messages []

   ;; Stores all of the games indexed by their id
   :games {}

   :simulator/state nil
   :simulator/templates nil
   :simulator/active-pane :code
   :simulator/wombat-id nil
   :simulator/template-id nil
   :simulator/mini-map false
   :simulator/error nil

   :join-game-selections []
   :spritesheet nil
   :modal-error nil})
