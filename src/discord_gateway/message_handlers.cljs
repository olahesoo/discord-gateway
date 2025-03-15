(ns discord-gateway.message-handlers
  (:require
   [discord-gateway.handler-utils :refer [dispatch]]
   [discord-gateway.message-parsers :refer [debug-msg
                                        update-sequence-number
                                        parse-hello
                                        parse-heartbeat
                                        parse-msg-with-no-op]]
   [discord-gateway.utils :refer [parser-identity]]))

(defn create-op-code-handler [code parser]
  {:match #(= (get % "op") code)
   :parse parser})

(def op-code-handlers
  (map #(apply create-op-code-handler %)
       [[1 parse-heartbeat]
        [10 parse-hello]
        [11 parser-identity]]))

(def sequence-number-handler
  {:match #(= (get % "s"))
   :parse update-sequence-number
   :keep-default true})

(def message-handlers
  (conj op-code-handlers sequence-number-handler))

(def message-to-gateway-handler
  (create-op-code-handler 0 #(dispatch % :accessor :gateway-handling)))
