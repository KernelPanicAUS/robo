(ns backend.tests
  (:require [backend.dax.client :refer [valid-row?]]
            [clojure.test :refer :all]))

(deftest addition-tests
  (is (= 5 (+ 3 2)))
  (is (= 11 (+ 5 5))))

(deftest filter-function
  (is (= true (valid-row? {:company "Linus TTips", :symbol "LTT"})))
  (is (= false (valid-row? {:company nil, :symbol "LTT"})))
  (is (= false (valid-row? {:company "LTT"})))
  (is (= false (valid-row? {:symbol "LTT"}))))

;(deftest create-price-entry
;  (is (= true (backend.yahoo.market-sync/create-price-entry [{:Date "23-10-2020", :AdjClose 78.0000}] #uuid "bbecd797-8cbd-4232-97c7-d3c0ab118254"))))