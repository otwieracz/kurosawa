(defproject org.purefn/kurosawa.web "2.1.27"
  :plugins [[lein-modules "0.3.11"]]
  :description "The Kurosawa web library."
  :dependencies [[com.stuartsierra/component "0.3.2"]

                 [ring/ring-core "1.10.0"]
                 [ring/ring-jetty-adapter "1.10.0"]
                 [http-kit "2.5.3"]
                 [org.immutant/web "2.1.7"
                  :exclusions [ch.qos.logback/logback-classic]]
                 [org.slf4j/log4j-over-slf4j "1.7.25"]
                 [com.fzakaria/slf4j-timbre "0.3.5"]

                 [org.purefn/kurosawa.log "2.1.27"]]

  :profiles {:dev {:dependencies [[bidi "2.0.17"]
                                  [clj-commons/iapetos "0.1.9"]
                                  [preflex "0.4.0"]]}})
