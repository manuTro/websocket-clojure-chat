(ns websocket-chat.client
  (:require [domina.core :refer [append!
                                 by-class
                                 by-id
                                 set-value!
                                 destroy!
                                 prepend!
                                 append!
                                 value
                                 attr]]
            [domina.events :refer [listen! prevent-default]]
            [hiccups.runtime])

(:require-macros [hiccups.core :refer [html]]))

(def ws (js/WebSocket. "ws://localhost:3000/ws"))

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
