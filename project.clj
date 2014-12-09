(defproject riemann-kafka "0.1.0"
  :description "A riemann plugin to consume from and produce to a kafka queue"
  :url "https://github.com/juise/riemann-kafka"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles {:dev {:dependencies [[org.clojure/clojure    "1.6.0"],
                                  [riemann                "0.2.6"]]}}
  :dependencies [
    [clj-kafka    "0.2.8-0.8.1.1"
                  :exclusions [org.slf4j/slf4j-log4j12
                               org.slf4j/slf4j-simple]]]
)
