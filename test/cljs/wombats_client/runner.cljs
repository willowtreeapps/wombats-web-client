(ns wombats-client.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [wombats-client.core-test]))

(doo-tests 'wombats-client.core-test)
