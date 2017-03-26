(ns chat.client
  (:require [domina.core :refer [append!
                                 by-id
                                 by-class
                                 set-value!
                                 destroy!
                                 value]]
            [domina.events :refer [listen!]]
            [hiccups.runtime]
            [cljs-http.client :as http]
            [cognitect.transit :as t]
            )
            (:require-macros [hiccups.core :refer [html]]))

(def ws (js/WebSocket. "ws://localhost:3000/ws"))

;(defn append-message [msg] (append! (by-id "history") (html [:div msg])) )

(defn append-message [e]
  (let [ msgs (.-msg (.parse js/JSON (.-data e)))
        author (.-author (.parse js/JSON (.-data e)))]
    (append! (by-id "history") (html [:div {:class "mes"} "Author: " author " Message: " msgs]))
    (listen! (by-class "mes") :click (fn[] (http/post "http://localhost:3000/ban" {:query-params {:user author}})) )))

;aset => set property. (set! (.-property obj) value)
(aset ws "onerror" (fn [](.log js/console  "error")))
(aset ws "onopen" (fn [e] (.log js/console "websocket open!") ))
(aset ws "onclose" (fn [e] (.log js/console "Error occurred")))
(aset ws "onmessage" (fn [evt]  (do (.log js/console "event: "evt "and data is" (.-data evt))(append-message evt))))

;(aset ws "onmessage" (fn [e]   (let [msgs  (.parse js/JSON (.-data e))] (append-message  msgs))))
;(aset ws "onmessage" (fn [e]   (let [msgs (.-msg (.parse js/JSON (.-data e)))] (append-message  msgs))))


; (defn server-message []
; (let [name (value (by-id "name"))
;       message (value (by-id "mess"))]
;       (.send ws message) ))

(defn server-message []
(let [name (value (by-id "name"))
      msg (value (by-id "mess"))]
      (.send ws (.stringify js/JSON (js-obj "msg" msg "author" name))) )) ;"{\"msg\":\"ddddd\",\"author\":\"anonymous\"}"

(defn ^:export init []
  (if (and js/document
           (.-getElementById js/document))
    (listen! (by-id "send") :click server-message)))
