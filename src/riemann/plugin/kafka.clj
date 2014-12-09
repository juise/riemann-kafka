(ns riemann.plugin.kafka
  "A riemann plugin to consume from and produce to a kafka queue"
  (:require [riemann.core          :refer [stream!]]
            [riemann.common        :refer [encode decode-msg]]
            [riemann.config        :refer [service!]]
            [riemann.service       :refer [Service ServiceEquiv]]
            [clj-kafka.core        :refer [to-clojure with-resource]]
            [clj-kafka.producer    :refer [producer message send-messages]]
            [clj-kafka.consumer.zk :refer [consumer messages shutdown]]
            [clojure.tools.logging :refer [info error]]))

(defn safe-decode
  "Do not let a bad payload break our consumption"
  [fn message]
  (try
    (fn (:value message))
    (catch Exception e
      (error e "could not decode protobuf msg"))))

(defn stringify
  "Prepare a map to be converted to properties"
  [config]
    (let [config (dissoc config :topic)]
      (zipmap (map name (keys config)) (vals config))))

(defn start-kafka-thread
  "Start a kafka thread which will pop messages off of the queue as long as running? is true"
  [running? core {:keys [topic] :as config} fn]
  (let [c (consumer (stringify config))]
    (future
      (with-resource [c (consumer (stringify config))]
        shutdown
        (doseq [message (messages c topic) :while @running? :when @core]
          (let [event (safe-decode fn message)]
            (stream! @core event)))))))

(defn kafka-consumer
  "Starts a new kafka consumer"
  [config fn]
  (service!
    (let [core     (atom nil)
          running? (atom true)]
      (reify
        clojure.lang.ILookup
        (valAt [this k not-found]
          (or (.valAt this k) not-found))
        (valAt [this k]
          (info "looking up: " k)
          (if (= (name k) "config") config))

        ServiceEquiv
        (equiv? [this other]
          (= config (:config other)))

        Service
        (conflict? [this other]
          (= config (:config other)))

        (reload! [this new-core]
          (reset! core new-core))

        (start! [this]
          (info "Kafka consumer for topics" (:topic config) "online")
          (start-kafka-thread running? core config fn))

        (stop! [this]
          (reset! running? false))))))

(defn kafka-producer
  "Starts a new kafka producer"
  [{:keys [topic] :as config}]
  (let [p (producer (stringify config))]
    (fn [event]
      (let [events (if (sequential? event) event [event])
            messages (map #(message topic (encode %)) events)]
        (send-messages p messages)))))

