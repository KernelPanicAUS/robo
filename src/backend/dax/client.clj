(ns backend.dax.client
  (:require [clj-http.client :as http]
            [backend.models.security :refer [create-security]]
            [backend.yahoo.client :refer [get-symbol-for-isin]]
            [dk.ative.docjure.spreadsheet :refer [load-workbook, select-sheet, select-columns, cell-seq, row-seq, read-cell]]))

(def url "https://www.deutsche-boerse-cash-market.com/resource/blob/67858/c4ae73c55ada26aa36328063cc830b09/data/Listed-companies.xlsx")

(defn stock-listing-xls-get
  []
  (:body (http/get url {:as :stream})))

(defn valid-row? [col]
  ((every-pred
    #(not (nil? (:company %)))
    #(not (nil? (:symbol %)))
    #(not (= "Trading Symbol" (:symbol %)))
    #(not (= "Company" (:company %)))) col))

(defn- resolve-symbol [row]
  (let [symbol (get-symbol-for-isin (:isin row))]
    (merge row {:symbol symbol})))

(defn- resolve-and-store [row]
  (->> row
       resolve-symbol
       create-security))

(defn get-securities []
  (with-open [stream (stock-listing-xls-get)]
    (->> (load-workbook stream)
         (select-sheet "Prime Standard")
         (select-columns {:B :symbol, :C :company, :A :isin, :D :sector, :F :country, :G :exchange})
         (remove nil?)
         (filter valid-row?)
         (run! resolve-and-store))))