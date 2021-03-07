(ns backend.models.common
  (:require [toucan.models :refer [add-property!]])
  (:import (java.sql Timestamp)
           (java.util UUID)))

(add-property! :timestamped?
               :insert (fn [obj _]
                         (let [now (Timestamp. (System/currentTimeMillis)) id (UUID/randomUUID)]
                           (assoc obj :created-at now, :updated-at now, :id id)))
               :update (fn [obj _]
                         (assoc obj :updated-at (Timestamp. (System/currentTimeMillis)))))
