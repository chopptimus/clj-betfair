(ns clj-betfair.betting
  (:require [clj-betfair.rpc :refer [def-rpc-endpoint-methods]]))

(def endpoint "https://api.betfair.com/exchange/betting/json-rpc/v1")
(def prefix "SportsAPING/v1.0/")

(def-rpc-endpoint-methods endpoint prefix
  list-event-types
  list-competitions
  list-time-ranges
  list-events
  list-market-types
  list-countries
  list-venues
  list-market-catalogue
  list-market-book
  list-runner-book
  list-market-profit-and-loss
  list-current-orders
  list-cleared-orders
  place-orders
  cancel-orders
  replace-orders
  update-orders)
