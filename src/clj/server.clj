(ns server
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler :refer [site]]
            [compojure.route :refer [not-found files resources]]
            [org.httpkit.server :as hk :refer [with-channel on-close on-receive websocket? run-server send!]]
            [org.httpkit.timer :as tim]
            [taoensso.timbre :as timbre]
            [clojure.edn :as edn]
            [clojure.data.json :refer [json-str read-json]]
            ;[ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            ;[ring.middleware.param :refer [wrap-param]]
            )
             (:use ring.middleware.params))


(defonce channels (atom []))



  (defn connect! [channel]
   (swap! channels conj channel)
   (println (count @channels) "channel opened" (str channel))
    ) ;adds channel to the channels atom

(defn handler [request]
 (with-channel request channel
               (connect! channel)
              (on-close channel (fn [status] (println "channel closed: " status)))
               (on-receive channel(fn [data]  (doseq [chan @channels]
                                                      (println data)
                                                      (send! chan data))))))

(def banned (atom {}))

  (defn add-to-banned [req]
   (let [usr-banned (:query-string req)]
   (swap! banned assoc usr-banned true)
   (println (keys @banned))))

(defroutes all-routes
  (GET "/" [] "Hello from compojure")(files "/" {:root "target"})
  (GET "/ws" request (handler request))
  (POST "/ban" author2 (add-to-banned author2))
  (not-found "Not Found"))
