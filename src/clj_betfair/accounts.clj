(ns clj-betfair.accounts
  (:require [clj-betfair.rpc :refer [def-rpc-endpoint-methods]]))

(def endpoint "https://api.betfair.com/exchange/accounts/json-rpc/v1")
(def prefix "AccountAPING/v1.0/")

(def-rpc-endpoint-methods endpoint prefix
  create-developer-app-keys
  get-developer-app-keys
  get-account-funds
  transfer-funds
  get-account-details
  get-account-statement
  list-currency-rates)
