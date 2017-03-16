(ns server
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler :refer [site]]
            [compojure.route :refer [not-found files resources]]
            [org.httpkit.server :as hk :refer [with-channel on-close on-receive websocket? run-server send!]]
            [org.httpkit.timer :as tim]
            [taoensso.timbre :as timbre]
            [clojure.edn :as edn]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))


(defonce channels (atom []))



  (defn connect! [channel]
   (swap! channels conj channel)
   (println (count @channels) "channel opened")
    ; (timbre/info  "channel open on " (str channel)  )
    ) ;adds channel to the channels atom
;
; (defn handler [request]
;  (with-channel request channel
;                (connect! channel)
;               (on-close channel (fn [status] (println "channel closed: " status)))
;                (on-receive channel(fn [data] (let [ data (edn/read-string data)
;                                                     name (:name data)
;                                                     message (:msg data)]
;                                                     (println "data received: " name "and " message)) ;; echo it back
;                           (send! channel data)))))

(defn handler [request]
 (with-channel request channel
               (connect! channel)
              (on-close channel (fn [status] (println "channel closed: " status)))
               (on-receive channel(fn [data]  (doseq [chan @channels]
                                                      (send! chan data))))))


(defroutes all-routes
  (GET "/" [] "Hello from compojure")(files "/" {:root "target"})
  (GET "/ws" request (handler request))
  (not-found "Not Found"))

; (def app
;   (wrap-defaults all-routes site-defaults))
;
;
; (defn -main [& args]
;     (run-server (site #'all-routes) {:port 8080})
;     )
