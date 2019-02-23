(ns clj-betfair.auth
  (:require
   [clojure.java.io :as io]
   [aleph.http :as http])
  (:import
   [java.security KeyStore]
   [javax.net.ssl KeyManagerFactory]
   [io.netty.handler.ssl SslContextBuilder]))

(defn- build-ssl-context
  [p12-path p12-password]
  (let [pass-array (char-array p12-password)
        client-store
        (doto (KeyStore/getInstance "pkcs12")
          (.load (io/input-stream p12-path) pass-array))
        key-manager-factory
        (doto (KeyManagerFactory/getInstance
               (KeyManagerFactory/getDefaultAlgorithm))
          (.init client-store pass-array))]
    (.. (SslContextBuilder/forClient)
        (keyManager key-manager-factory)
        build)))

(defn login!
  [username password app-key p12-path p12-password]
  (let [ssl-context (build-ssl-context p12-path p12-password)
        pool (http/connection-pool
              {:connection-options {:ssl-context ssl-context}})]
    (http/post "https://identitysso-cert.betfair.com/api/certlogin"
               {:query-params {:username username :password password}
                :headers {"X-Application" app-key
                          "Content-Type" "application/x-www-form-urlencoded"}
                :pool pool})))

(defn keepalive!
  [app-key session-token]
  (http/post "https://identitysso.betfair.com/api/keepAlive"
             {:headers {"Accept" "application/json"
                        "X-Application" app-key
                        "X-Authentication" session-token}}))
