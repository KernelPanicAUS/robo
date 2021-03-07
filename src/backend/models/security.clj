(ns backend.models.security
  (:require [toucan.db :as db]
            [backend.models.common :refer :all]
            [toucan.models :refer :all]
            [backend.db :refer [datasource]]
            [clojure.java.jdbc :as jdbc])
  (:gen-class))

(declare Security)

(defmodel Security :security
  IModel
  (properties [_]
              {:timestamped? true})
  (primary-key [_] :id))

(defn check2 [security]
  (jdbc/with-db-connection [conn {:datasource datasource}]
    (let [rows (jdbc/query conn ["select * from security where isin=?" (:isin security)])]
      rows)))
(defn check [security]
  ;;TODO: Check if toucan can deal with sparse maps
  (db/select Security security))

(defn get-all
  "Load all Securities from database"
  []
  (db/select Security))

(defn create-security [new-security]
  (try
    (let [security (check2 new-security)]
      (if (empty? security) (db/insert! Security new-security)))
    (catch Exception e
      (println "exception: " (.getMessage e)))))
