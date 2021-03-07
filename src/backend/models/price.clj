(ns backend.models.price
  (:require [toucan.db :as db]
            [toucan.models :refer :all]
            [backend.models.common :refer :all])
  (:gen-class))

(declare Price)

(defmodel Price :price
          IModel
          (properties [_]
                      {:timestamped? true})
          (primary-key [_] :id))

(defn create-price [price]
  ;(if-not (db/select Price price) (db/insert! Price price)))
  (db/insert! Price price))