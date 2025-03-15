(ns discord-gateway.gateway-parsers
  (:require [discord-gateway.utils :refer [print-debug]]))

(defn parse-ready [_] #(assoc % :conn :connected))

(defn extract-emoji-info [msg]
  (let [d (get msg "d")]
    {:user (get d "user_id")
     :emoji (get-in d ["emoji" "name"])
     :guild (get d "guild_id")
     :channel (get d "channel_id")}))

(defn parse-debug-voice-channel-effect-send [msg]
  (let [d (get msg "d")
        info (extract-emoji-info msg)]
    (print-debug info)))
