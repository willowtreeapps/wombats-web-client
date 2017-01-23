(ns wombats-web-client.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [wombats-web-client.core :as core]))

(deftest fake-test
  (testing "fake description"
    (is (= 1 2))))
