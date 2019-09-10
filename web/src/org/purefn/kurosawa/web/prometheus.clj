(ns org.purefn.kurosawa.web.prometheus
  (:require [bidi.bidi :as bidi]
            [clojure.string :as str]
            [iapetos.core :as prometheus]
            [iapetos.collector.ring :as ring]
            [taoensso.timbre :as log]))

(defn path
  [routes ignore-keys {:keys [uri request-method]}]
  (or (->> (bidi/match-route routes uri :request-method request-method)
           (:route-params)
           (remove (comp (set ignore-keys)
                         first))
           (reduce (fn [path [p v]]
                     (str/replace path (re-pattern v) (str p)))
                   uri))
      uri))

(defonce registry
  (-> (prometheus/collector-registry)
      (ring/initialize)))

(defn wrap-metrics
  ([handler routes registry]
   (wrap-metrics handler routes registry {}))
  ([handler routes registry {:keys [ignore-keys] :as options}]
   (let [path-fn (partial path routes ignore-keys)]
     (ring/wrap-metrics handler registry {:path-fn path-fn}))))
