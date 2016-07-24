
(ns wombats_web_client.subs.games
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/register-sub
 :games
 (fn [db _]
   (reaction (:games @db))))
