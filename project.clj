(def reitit-version "0.5.11")
(defproject backend "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.2"]
                 [clj-http "3.12.0"]
                 [crouton "0.1.2"]
                 [cheshire "5.10.0"]
                 [dk.ative/docjure "1.14.0"]
                 [org.clojure/data.csv "1.0.0"]
                 [toucan "1.15.3"]
                 [org.postgresql/postgresql "42.2.6"]
                 [hikari-cp "2.8.0"]
                 [org.flywaydb/flyway-core "5.2.4"]
                 [mount "0.1.16"]
                 [aero "1.1.6"]
                 [com.taoensso/timbre "4.10.0"]
                 [clojure.java-time "0.3.2"]
                 [schejulure "1.0.1"]
                 [com.fzakaria/slf4j-timbre "0.3.20"]
                 [io.aviso/pretty "0.1.37"]
                 [metosin/reitit ~reitit-version :exclusions [org.clojure/spec.alpha ring/ring-core commons-fileupload]]
                 [metosin/reitit-swagger ~reitit-version :exclusions [ring/ring-codec]]
                 [metosin/reitit-swagger-ui ~reitit-version :exclusions [ring/ring-core commons-fileupload]]
                 [ring/ring-jetty-adapter "1.7.1" :exclusions [clj-time]]
                 [com.fasterxml.jackson.core/jackson-core "2.12.2"]]
  :main backend.core
  :repl-options {:init-ns backend.core}
  :source-paths ["src/"]
  :middleware [io.aviso.lein-pretty/inject]
  :plugins [[lein-cljfmt "0.7.0"]
            [io.aviso/pretty "0.1.37"]]
  :profiles {:uberjar {:aot      :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}
  :dev {:source-paths ["dev"] :dependencies [[org.clojure/tools.namespace "0.3.1"]]})
