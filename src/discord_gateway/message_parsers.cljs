(ns discord-gateway.message-parsers
  (:require
   [discord-gateway.heartbeat :refer [send-beat start-beater]]
   [discord-gateway.utils :refer [print-debug]]))

(defn debug-msg [msg] (print-debug msg))

(defn update-sequence-number [msg]
  (fn [state]
    (if-let [s (get msg "s")]
      (assoc state :s s)
      state)))

(defn parse-hello [msg]
  (comp
   (if-let [interval (get-in msg ["d" "heartbeat_interval"])]
     (start-beater interval)
     (print-debug {:description "Key 'd.heartbeat_interval' not found"
                   :msg msg}))
   #(assoc % :conn :pending)))

(defn parse-heartbeat [msg] send-beat)

(defn parse-msg-with-no-op [msg]
  (print-debug {:description "Key 'op' not found"
                :msg msg}))
