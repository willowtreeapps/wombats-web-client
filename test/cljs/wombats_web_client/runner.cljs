(ns wombats-web-client.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [wombats-web-client.core-test]))

(doo-tests 'wombats-web-client.core-test)
