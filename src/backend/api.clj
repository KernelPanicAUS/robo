(ns backend.api
  (:require [reitit.ring :as ring]
            [reitit.http :as http]
            [reitit.coercion.spec]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.http.coercion :as coercion]
            [reitit.dev.pretty :as pretty]
            [reitit.interceptor.sieppari :as sieppari]
            [reitit.http.interceptors.parameters :as parameters]
            [reitit.http.interceptors.muuntaja :as muuntaja]
            [reitit.http.interceptors.exception :as exception]
            [reitit.http.interceptors.multipart :as multipart]
            [reitit.ring.middleware.dev :refer [print-request-diffs]]
            [ring.adapter.jetty :as jetty]
            [muuntaja.core :as m]
            [mount.core :as mount])
  (:gen-class))

(def app
  (http/ring-handler
    (http/router
      [["/swagger.json"
        {:get {:no-doc  true
               :swagger {:info {:title       "my-api"
                                :description "with reitit-http"}}
               :handler (swagger/create-swagger-handler)}}]]

      { ;:reitit.interceptor/transform dev/print-context-diffs ;; pretty context diffs
       ;;:validate spec/validate ;; enable spec validation for route data
       ;;:reitit.spec/wrap spell/closed ;; strict top-level validation
       :reitit.middleware/transform print-request-diffs
       :exception pretty/exception
       :data      {:coercion     reitit.coercion.spec/coercion
                   :muuntaja     m/instance
                   :interceptors [;; swagger feature
                                  swagger/swagger-feature
                                  ;; query-params & form-params
                                  (parameters/parameters-interceptor)
                                  ;; content-negotiation
                                  (muuntaja/format-negotiate-interceptor)
                                  ;; encoding response body
                                  (muuntaja/format-response-interceptor)
                                  ;; exception handling
                                  (exception/exception-interceptor)
                                  ;; decoding request body
                                  (muuntaja/format-request-interceptor)
                                  ;; coercing response bodies
                                  (coercion/coerce-response-interceptor)
                                  ;; coercing request parameters
                                  (coercion/coerce-request-interceptor)
                                  ;; multipart
                                  (multipart/multipart-interceptor)]}})
    (ring/routes
      (swagger-ui/create-swagger-ui-handler
        {:path   "/"
         :config {:validatorUrl     nil
                  :operationsSorter "alpha"}})
      (ring/create-default-handler))
    {:executor sieppari/executor}))

(mount/defstate api :start (jetty/run-jetty #'app {:port 6100, :join? false, :async true}))
