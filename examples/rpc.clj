(require '[cheshire.core :as c]
         '[clj-betfair.rpc :refer [unwrap]]
         '[clj-betfair.betting :as betting])

(let [app-key "app-key-here"
      session-token "session-token-here"]
  @(unwrap (betting/list-events
            app-key
            session-token
            {:filter {:eventTypeIds [1]}})))
