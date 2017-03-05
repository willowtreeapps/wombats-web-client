(ns wombats-web-client.db
  (:require [wombats-web-client.constants.local-storage :refer [token]]
            [wombats-web-client.utils.local-storage :refer [get-item]]))

(def default-db
  {:active-panel nil
   :auth-token (get-item token)
   :bootstrapping? false
   :name "WillowTree"
   :current-user nil
   :users []
   :modal nil
   :open-games nil
   :my-open-games nil
   :closed-games nil
   :my-closed-games nil
   
   :game/arena nil
   :game/info nil
   :game/messages []
   
   ;; Stores all of the games indexed by their id
   :games {}

   :simulator/state nil
   :simulator/templates nil

   :join-game-selections []
   :spritesheet nil
   :modal-error nil})
