(ns wombats-web-client.db
  (:require [wombats-web-client.utils.local-storage :refer [get-token]]))

(def default-db
  {:active-panel nil
   :auth-token (get-token)
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

   :simulator/state nil
   :simulator/templates nil
   :simulator/active-pane :code
   :simulator/wombat-id nil
   :simulator/template-id nil

   :join-game-selections []
   :spritesheet nil
   :modal-error nil})
