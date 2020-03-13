(ns clj-betfair.rpc
  (:require [aleph.http]
            [cheshire.core :as c]
            [clojure.string :as string]))

(defn camel-casify
  [s]
  (let [toks (string/split s #"-")]
    (apply str (first toks) (map string/capitalize (rest toks)))))

(defn rpc-request-map
  [app-key session-token method params]
  {:headers {:x-application app-key
             :x-authentication session-token
             :accept "application/json"
             :content-type "application/json"}
   :body (c/generate-string {:jsonrpc "2.0"
                             :method method
                             :params params
                             :id 1})})

(defn make-rpc-request-fn
  [endpoint prefix sym]
  (let [method (str prefix (camel-casify (str sym)))]
    (fn rpc-request
      ([app-key session-token]
       (rpc-request app-key session-token {}))
      ([app-key session-token params]
       (let [req-map (rpc-request-map app-key session-token method params)]
         (aleph.http/post endpoint req-map))))))

(defmacro def-rpc-endpoint-methods
  [endpoint prefix & rpc-methods]
  `(do
     ~@(map (fn [sym]
              `(def ~sym (make-rpc-request-fn ~endpoint ~prefix (quote ~sym))))
            rpc-methods)))
