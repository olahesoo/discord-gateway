(ns discord-gateway.socket-controller
  (:require
   [discord-gateway.handler-utils :refer [dispatch handlers-adder]]
   [discord-gateway.ws-event-handlers :refer [base-to-event-handler
                                          debug-all-handler
                                          event-to-message-handler
                                          ws-event-handlers]]
   [discord-gateway.ws-event-parsers :refer [parse-console-debug]]
   [discord-gateway.message-handlers :refer [message-handlers message-to-gateway-handler]]
   [discord-gateway.message-parsers :refer [debug-msg]]
   [discord-gateway.gateway-handlers :refer [gateway-handlers]]
   [discord-gateway.discord-socket :refer [open-discord-socket]]
   [cljs.core.async :refer [<! alt! chan go]]))

(defn add-standard-handlers [state & {:keys [debug]}]
  (let [event-handling (conj ws-event-handlers event-to-message-handler)
        message-handling (conj message-handlers message-to-gateway-handler)]
    (->> state
         ((handlers-adder event-handling :key :event-handling :default-parser parse-console-debug))
         ((handlers-adder message-handling :key :message-handling :default-parser debug-msg))
         ((handlers-adder gateway-handlers :key :gateway-handling :default-parser debug-msg))
         ((handlers-adder [base-to-event-handler] :default-parser parse-console-debug))
         ((if debug (handlers-adder [debug-all-handler]) identity)))))

(defn create-socket-controller
  "Create a socket controller wrapping a Discord gateway connection.
  Returns a map with `command-c`, `close-f` and `state`.
  `command-c`: Async channel, send tasks here to execute them.
  `close-f`: Function that shuts down the socket when called.
  `state`: Internal state for inspection, do not modify directly."
  [& {:as handler-opts}]
  (let [[socket->clj clj->socket shutdown] (open-discord-socket)
        command-c (chan)
        state (atom (add-standard-handlers {:command-c command-c
                                            :send-c clj->socket} handler-opts))]
    (go
      ;; Recieve 'open' event before accepting commands.
      (reset! state ((dispatch (<! socket->clj)) @state))
      (loop []
        (let [task (alt! command-c ([cmd] cmd)
                         socket->clj ([event] (dispatch event)))
              next-state (task @state)]
          (reset! state next-state))
        (recur)))
    {:command-c command-c
     :state state
     :close-f shutdown}))
