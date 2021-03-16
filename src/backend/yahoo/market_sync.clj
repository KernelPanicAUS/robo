(ns backend.yahoo.market-sync
  (:refer-clojure :exclude [contains? format zero? min max iterate range])
  (:require [backend.models.security :refer [get-all]]
            [taoensso.timbre :as log]
            [backend.yahoo.client :as client]
            [backend.models.price :refer [create-price]]
            [java-time :refer :all]
            [schejulure.core :as sched]
            [backend.config :as config]
            [mount.core :as mount])
  (:import (java.util.concurrent Executors)))

(mount/defstate config :start (config/yahoo))
(mount/defstate pool
                :start (Executors/newFixedThreadPool (get-in config [:pool-size]))
                :stop (.shutdown pool))

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

    (log/info (str "Task submitted for " symbol))
    #(->> (client/get-security-price symbol (get-in config [:days-ago]))
          (remove nil?)
          (run! cpe))))

(defn sync-securities []
  (log/info "Starting sync for securities against yahoo! finance api...")
  (log/info (str config))
  (let [tasks (map sync-security (get-securities))
        ret (.invokeAll pool tasks)]
    (map #(.get %) ret))
  (log/info "Sync completed!"))

(defn sync-scheduler []
  (sched/schedule (get-in config [:schedule]) sync-securities))


