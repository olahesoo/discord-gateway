(ns discord-gateway.ws-event-parsers
  (:require
   [discord-gateway.heartbeat :refer [close-beater-if-active]]
   [cognitect.transit :as t]))

(defn parse-console-debug [event]
  (fn [state]
    (.log js/console event)
    state))

(defn parse-ws-open [_]
  (fn [state]
    (.log js/console "Socket opened")
    (assoc state :conn :open)))

(defn parse-ws-close [event]
  (comp
   (fn [state]
     (.log js/console "Socket closed, reason: " (.-reason event))
     (assoc state :conn :closed))
   close-beater-if-active))
