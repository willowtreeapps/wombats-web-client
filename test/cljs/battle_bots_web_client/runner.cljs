(ns battle_bots_web_client.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [battle_bots_web_client.core-test]))

(doo-tests 'battle_bots_web_client.core-test)
