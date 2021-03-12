(ns backend.core
  (:require [backend.dax.client :refer [stock-listing-xls-get]]
            [backend.dax.client :refer [get-securities]]
            [mount.core :as mount]
            [backend.db :as db]
            [backend.config :as config]
            [backend.api :as api]
            [clojure.spec.alpha :as s]
            [taoensso.timbre :as log]
            [taoensso.timbre.appenders.3rd-party.rolling :refer [rolling-appender]]
            [backend.yahoo.market-sync :refer [sync-securities]])
  (:gen-class))

(defn start-app
  ([]
   (start-app true))
  ([check-asserts]
   ;(log/init)
   (s/check-asserts check-asserts)
   (mount/start)
   ;(cron/init)
   ;(sync-securities)
   ))

(defn -main []
  (log/merge-config!
   {:appenders {:spit (rolling-appender {:path "logs/backend.log", :patten :daily})}})
  (log/set-level! :info)
  (log/info "Starting app...")
  (start-app)
  (backend.yahoo.market-sync/sync-scheduler))

(defn stop-app []
  (mount/stop))

(defn migrate-database []
  (mount/start #'config/root #'db/datasource #'api/api)
  (db/migrate)
  ;(mount/stop)
  )

;(comment
;  (start-app)
;  (stop-app)
;  (migrate-database))