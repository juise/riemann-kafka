(defproject org.spootnik/riemann-kafka "0.1.0"
  :description "riemann producer and consumer for kafka queues"
  :url "https://github.com/pyr/riemann-kafka"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles { :provided { :dependencies [[org.clojure/clojure "1.6.0"], [riemann "0.2.6"]] } }
  :dependencies [[clj-kafka           "0.2.6-0.8"
                  :exclusions [org.slf4j/slf4j-log4j12
                               org.slf4j/slf4j-simple
                               org.clojure/clojure]]])
