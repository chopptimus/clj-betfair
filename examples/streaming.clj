(ns streaming
  (:gen-class)
  (:require [cheshire.core :refer [parse-string]]
            [clojure.edn :as edn]
            [clojure.stacktrace :refer [print-stack-trace]]
            [clj-betfair.auth :as auth]
            [clj-betfair.stream :as bfs]
            [manifold.stream :as s]))

(defn keepalive-loop!
  [app-key session-token]
  (try
    (loop []
      (let [keepalive-response @(auth/keepalive! app-key session-token)
            status (get (parse-string (:body keepalive-response)) "status")]
        (when (= status "SUCCESS")
          (Thread/sleep (* 60 60))
          (recur))))
    (catch Exception e
      (print-stack-trace e)
      (when-not (instance? InterruptedException e)
        (throw e)))))

(defn -main
  [config]
  (let [{:keys [betfair-creds]} (edn/read-string (slurp config))
        {:keys [username password app-key p12-path p12-password]} betfair-creds
        resp @(auth/login! username password app-key p12-path p12-password)
        body (slurp (:body resp))
        session-token (get (parse-string body) "sessionToken")
        stream (bfs/betfair-stream app-key session-token {:decode-json false})]
    (s/put!
     stream
     (bfs/market-subscription
      {"marketFilter" {"eventIds" [2]
                       "marketTypes" ["MATCH_ODDS"]}}
      {"marketDataFilter" {"fields" ["MARKET_DEF" "EX_ALL_OFFERS"]}}))
    (s/consume #(println %) stream)))
