(ns wombats_web_client.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [wombats_web_client.core-test]))

(doo-tests 'wombats_web_client.core-test)
