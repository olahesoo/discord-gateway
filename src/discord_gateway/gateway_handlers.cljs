(ns discord-gateway.gateway-handlers
  (:require
   [discord-gateway.gateway-parsers :refer [parse-ready]]
   [discord-gateway.utils :refer [parser-identity]]))

(defn create-gateway-handler [op parser]
  {:match #(= (get % "t") op)
   :parse parser})

(def gateway-handlers
  (map #(apply create-gateway-handler %)
       [["READY" parse-ready]
        ["GUILD_CREATE" parser-identity]
        ["VOICE_STATE_UPDATE" parser-identity]
        ["VOICE_SERVER_UPDATE" parser-identity]
        ["VOICE_CHANNEL_EFFECT_SEND" parser-identity]]))
