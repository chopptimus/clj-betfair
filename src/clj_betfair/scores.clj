(ns clj-betfair.scores
  (:require [clj-betfair.rpc :refer [def-rpc-endpoint-methods]]))

(def endpoint "https://api.betfair.com/exchange/scores/json-rpc/v1")
(def prefix "ScoresAPING/v1.0/")

(def-rpc-endpoint-methods endpoint prefix
  list-available-events
  list-scores
  list-incidents
  list-race-status)
