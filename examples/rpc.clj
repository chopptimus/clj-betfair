(ns rpc
  (:require [cheshire.core :as c]
            [clj-betfair.betting :as betting]
            [clj-betfair.scores :as scores]
            [manifold.deferred :as d]))

(def session-token "redacted")
(def app-key "redacted")

@(d/chain
  (scores/list-available-events app-key session-token {})
  (fn [resp] (-> resp :body slurp c/parse-string (get "result"))))
