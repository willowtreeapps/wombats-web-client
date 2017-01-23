(ns wombats-client.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [wombats-client.core :as core]))

(deftest fake-test
  (testing "fake description"
    (is (= 1 2))))
