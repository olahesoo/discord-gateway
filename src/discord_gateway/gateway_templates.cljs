(ns discord-gateway.gateway-templates)

(defn create-identify [token]
  {:op 2
   :d {:token token
       :properties {:os "linux"
                    :browser "disco"
                    :device "disco"}}
   :intents 128})

(defn join-voice-msg [guild-id channel-id]
  {:op 4
   :d {:guild_id guild-id
       :channel_id channel-id
       :self_mute true
       :self_deaf true}})

(defn leave-voice-msg [guild-id]
  (join-voice-msg guild-id nil))
