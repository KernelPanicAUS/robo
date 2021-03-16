(ns backend.config
  (:require [mount.core :as mount]
            [aero.core :as aero]
            [clojure.java.io :as io]))

(defn read-config []
  (aero/read-config (io/resource "config.edn")))

(mount/defstate root :start (read-config))

(defn database []
  (get-in root [:app :database]))

(defn yahoo []
  (get-in root [:app :jobs :yahoo]))
