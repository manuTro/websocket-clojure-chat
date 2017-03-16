(ns server
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler :refer [site]]
            [compojure.route :refer [not-found files resources]]
            [org.httpkit.server :as hk :refer [with-channel on-close on-receive websocket? run-server send!]]
            [org.httpkit.timer :as tim]
            [taoensso.timbre :as timbre]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))


(defonce channels (atom #{}))

  (defn connect! [channel]
   (timbre/info "channel open")
   (swap! channels conj channel))
;
(defn handler [request]
 (with-channel request channel
               (connect! channel)
              (on-close channel (fn [status] (println "channel closed: " status)))
               (on-receive channel(fn [data] (println "data received: " data) ;; echo it back
                          (send! channel data)))))


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
