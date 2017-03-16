(ns chat.client
  (:require [domina.core :refer [
                                 by-id
                                 set-value!
                                 destroy!
                                 value]]
            [domina.events :refer [listen!]]
            ))

(def ws (js/WebSocket. "ws://localhost:3000/ws"))

;aset => set property. (set! (.-property obj) value)
(aset ws "onerror" (fn [](.log js/console  "error")))
(aset ws "onopen" (fn [e] (.log js/console "websocket open!") ))
(aset ws "onclose" (fn [e] (.log js/console "Error occurred")))
(aset ws "onmessage" (fn [e] ((if (string? (.-data e) ) (.log js/console "Received message")))))

(defn server-message []
  (let [name (value (by-id "name"))
        message (value (by-id "mess"))]
        (.send ws (str name ":" message)) ))

(defn ^:export init []
  (if (and js/document
           (.-getElementById js/document))
    (listen! (by-id "send") :click server-message)))
