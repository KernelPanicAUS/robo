(ns backend.yahoo.client
  (:refer-clojure :exclude [contains? format zero? min max future iterate range future-call])
  (:require [clj-http.client :as http]
            [cheshire.core :refer :all]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [taoensso.timbre :as timbre])
  (:use java-time)
  (:gen-class)
  (:import (java.time ZoneId ZonedDateTime LocalTime)))

(def symbol_lookup_url "https://query1.finance.yahoo.com/v1/finance/search")

;https://query1.finance.yahoo.com/v7/finance/download/DRI.DE?period1=1582848000&period2=1614729600&interval=1d&events=history&includeAdjustedClose=true
(def market_close_url "https://query1.finance.yahoo.com/v7/finance/download/")

(defn- get-epoch-seconds-for-days-ago [days-ago]
  (-> (ZonedDateTime/now (ZoneId/of "UTC"))
      (.minusDays days-ago)
      (.with LocalTime/MIN)
      (.toEpochSecond)))

(defn- get-epoch-seconds []
  (-> (ZonedDateTime/now (ZoneId/of "UTC"))
      (.with LocalTime/MIN)
      (.toEpochSecond)))

(defn- parse [str]
  (parse-string str true))

(defn get-symbol-for-isin
  [isin]
  (->> (:body (http/get symbol_lookup_url {:accept :json :query-params {"q" isin, "newsCount" "0"}}))
       parse
       :quotes
       first
       :symbol))

(defn- stream-csv [symbol days]
  (timbre/info (str "Fetching market performance data for " symbol " going back " days " days..."))
  (let [period1 (get-epoch-seconds-for-days-ago days),
        period2 (get-epoch-seconds)]
    (try
      (:body (http/get (str market_close_url symbol) {:as :stream :query-params {"period1"              period1,
                                                                                 "period2"              period2,
                                                                                 "interval"             "1d",
                                                                                 "filter"               "history",
                                                                                 "frequency"            "1d",
                                                                                 "includeAdjustedClose" "true"}}))
      (catch Exception e
        (timbre/error "error occurred" (.getMessage e))))))

(defn- csv-data->maps [csv-data]
  ;(timbre/debug "Mapping csv headers to map keys...")
  (map zipmap
       (->> (first csv-data)                                ;; First row is the header
            (map keyword)                                   ;; Drop if you want string keys instead
            repeat)
       (rest csv-data)))

(defn- remap-keys [csv-data]
  (map #(clojure.set/rename-keys % {(keyword "Adj Close") :AdjClose}) csv-data))

(defn get-security-price [symbol date]
  (with-open [reader (io/reader (stream-csv symbol date))]
    (->> (csv/read-csv reader)
         (csv-data->maps)
         (remap-keys)
         (doall))))
