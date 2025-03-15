(ns discord-gateway.ws-event-handlers
  (:require
   [discord-gateway.handler-utils :refer [dispatch]]
   [discord-gateway.ws-event-parsers :refer [parse-ws-open parse-ws-close]]
   [cognitect.transit :as t]))

(defn event->msg [event]
  (t/read (t/reader :json) (.-data event)))

(defn create-ws-event-handler [event-type parser]
  {:match #(= (.-type %) event-type)
   :parse parser})

(def ws-event-handlers
  (map #(apply create-ws-event-handler %)
       [["open" parse-ws-open]
        ["close" parse-ws-close]]))

(def event-to-message-handler
  (create-ws-event-handler
   "message"
   #(dispatch (event->msg %) :accessor :message-handling)))

(def base-to-event-handler
  {:match #(.-type %)
   :parse #(dispatch % :accessor :event-handling)})

(def debug-all-handler
  {:match #(do true)
   :parse (fn [event] #(do (.log js/console event) %))})
