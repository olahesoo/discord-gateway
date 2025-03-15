(ns discord-gateway.discord-socket
  (:require
   [cljs.core.async :refer [<! >! alt! chan go]]
   [cljs.core.async.interop :refer [<p!]]))

(def gateway-url-endpoint "https://discord.com/api/gateway")

(defn- async-get-gateway-url []
  (go
    (let [response (<p! (js/fetch gateway-url-endpoint))
          data (<p! (.json response))]
      (.-url data))))

(defn- make-new-socket [url]
  (new js/WebSocket url))

(defn- set-all-socket-events [socket fn]
  (set! (.-onopen socket) fn)
  (set! (.-onmessage socket) fn)
  (set! (.-onerror socket) fn)
  (set! (.-onclose socket) fn))

(defn open-discord-socket
  "Creates a Discord websocket.

  Returns three values:
  - channel that receives all events that the socket emits
  - channel whose contents will be sent to the socket
  - function that shuts down the socket when called"
  []
  (let [socket->clj (chan)
        clj->socket (chan)
        shutdown (chan)]
    (go
      (let [url (<! (async-get-gateway-url))
            socket (make-new-socket url)]
        (set-all-socket-events socket #(go (>! socket->clj %)))
        (while
            (alt!
              clj->socket ([v] (do (.send socket (.stringify js/JSON (clj->js v)))) true)
              shutdown nil))
        (.log js/console "Shutting down socket")
        (.close socket)))
    [socket->clj clj->socket #(go (>! shutdown :close))]))
