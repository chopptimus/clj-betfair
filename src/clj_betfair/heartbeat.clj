(ns clj-betfair.heartbeat
  (:require [clj-betfair.rpc :refer [def-rpc-endpoint-methods]]))

(def endpoint "https://api.betfair.com/exchange/heartbeat/json-rpc/v1")
(def prefix "HeartbeatAPING/v1.0/")

(def-rpc-endpoint-methods endpoint prefix
  heatbeat)
