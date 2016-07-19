(ns wombats_web_client.subs.root
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]
              [wombats_web_client.subs.ui]
              [wombats_web_client.subs.user]))
