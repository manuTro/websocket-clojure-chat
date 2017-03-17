(ns chat.client
  (:require [domina.core :refer [append!
                                 by-id
                                 set-value!
                                 destroy!
                                 value]]
            [domina.events :refer [listen!]]
            [hiccups.runtime]
            [cognitect.transit :as t]
            )
            (:require-macros [hiccups.core :refer [html]]))

(def ws (js/WebSocket. "ws://localhost:3000/ws"))

(defn append-message [msg] (append! (by-id "history") (html [:div msg])) )


;aset => set property. (set! (.-property obj) value)
(aset ws "onerror" (fn [](.log js/console  "error")))
(aset ws "onopen" (fn [e] (.log js/console "websocket open!") ))
(aset ws "onclose" (fn [e] (.log js/console "Error occurred")))
;(aset ws "onmessage" (fn [e]  (append-message (aget e "data"))))
(aset ws "onmessage" (fn [e]   (let [msgs  (.parse js/JSON (.-data e))] (append-message  msgs))))
;(aset ws "onmessage" (fn [e]   (let [msgs (.-msg (.parse js/JSON (.-data e)))] (append-message  msgs)))) not working

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
