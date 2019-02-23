(ns clj-betfair.stream
  (:require [manifold.deferred :as d]
            [manifold.stream :as s]
            [aleph.tcp :as tcp]
            [gloss.core :as gloss]
            [gloss.io :as io]
            [cheshire.core :as c]))

(def ^:private stream-api-host "stream-api.betfair.com")
(def ^:private stream-api-port 443)

(def ^:private request-id (atom 0))

(defn make-protocol
  [decode-json encode-json]
  (gloss/compile-frame
    (gloss/string :utf-8 :delimiters ["\r\n"])
    (if encode-json
      c/generate-string
      identity)
    (if decode-json
      c/parse-string
      identity)))

(defn- wrap-duplex-stream
  [protocol s]
  (let [out (s/stream)]
    (s/connect
      (s/map #(io/encode protocol %) out)
      s)
    (s/splice
      out
      (io/decode-stream s protocol))))

(defn- auth-message
  [session-token app-key]
  {"op" "authentication"
   "id" (swap! request-id inc)
   "appKey" app-key
   "session" session-token})

(defn betfair-stream
  ([app-key session-token]
   (betfair-stream app-key session-token {}))
  ([app-key session-token opts]
   (let [{:keys [decode-json encode-json]
          :or {decode-json true encode-json true}} opts
         protocol (make-protocol decode-json encode-json)
         s @(d/chain
             (tcp/client {:host stream-api-host
                          :port stream-api-port
                          :ssl? true})
             #(wrap-duplex-stream protocol %))]
     (s/take! s)
     (s/put! s (auth-message session-token app-key))
     (s/take! s)
     s)))

(defn market-subscription
  [market-filter market-data-filter]
  {"op" "marketSubscription"
   "id" (swap! request-id inc)
   "marketFilter" market-filter
   "marketDataFilter" market-data-filter})
