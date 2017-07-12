(ns wombats-web-client.db
  (:require [wombats-web-client.utils.local-storage :refer [get-token]]))

(def default-db
  {:active-panel nil
   :auth-token (get-token)
   :bootstrapping true
   :current-user nil
   :users []
   :modal nil

   :game/arena nil
   :game/messages []

   ;; Stores all of the games indexed by their id
   :games {}

   :arenas nil
   :simulator/frames-vec []
   :simulator/frames-idx 0
   :simulator/state nil
   :simulator/templates nil
   :simulator/wombat-id nil
   :simulator/template-id nil
   :simulator/error nil
   :simulator/view-mode :frame

   :join-game-selections []
   :spritesheet nil
   :modal-error nil
   :login-error nil
   :access-keys nil})
