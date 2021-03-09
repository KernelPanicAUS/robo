(ns backend.yahoo.market-sync
  (:refer-clojure :exclude [contains? format zero? min max future iterate range future-call])
  (:require [backend.models.security :refer [get-all]]
            [taoensso.timbre :as log]
            [backend.yahoo.client :as client]
            [backend.models.price :refer [create-price]]
            [schejulure.core :as sched])
  (:gen-class)
  (:use java-time)
  (:use qbits.knit))

(def days-ago 2)
(def x (executor :fixed {:num-threads 10}))

(defn- get-securities []
  (log/info "Loading securities from db...")
  (let [securities (get-all)]
    (log/info (str "loaded [" (count securities) "] securities"))
    securities))

(defn- create-price-entry [security-id symbol entry]
  (let [date (local-date "yyyy-MM-dd" (:Date entry)),
        amount (Float/parseFloat (:AdjClose entry)),
        entry-map {:security_id security-id,
                   :date        date,
                   :amount      amount}]
    (log/info (str "Creating price entry for " symbol " - " date " - " amount))
    (create-price entry-map)))

(defn- sync-security [security]
  (let [symbol (:symbol security)
        security-id (:id security)
        cpe (partial create-price-entry security-id symbol)]
    (log/info (str "Running sync for " symbol "..."))
    (execute x
             #(->> (client/get-security-price symbol days-ago)
                   (remove nil?)
                   (run! cpe)))
    (log/info (str "Task submitted for " symbol))))

(defn sync-securities []
  (log/info "Starting sync for securities against yahoo! finance api...")
  (let [securities (get-securities)]
    (doall (map #(sync-security %) securities))
    (log/info "Sync completed!")))

(defn sync-scheduler []
  (sched/schedule {:hour   01
                   :minute 00
                   :day    [:tue :wed :thur :fri :sat]}
                  sync-securities))
