(ns discord-gateway.core
  (:require
   [discord-gateway.gateway-parsers :refer [extract-emoji-info]]
   [discord-gateway.gateway-handlers :refer [create-gateway-handler]]
   [discord-gateway.gateway-templates :refer [create-identify join-voice-msg leave-voice-msg]]
   [discord-gateway.handler-utils :refer [create-handler-adder handlers-adder]]
   [discord-gateway.socket-controller :refer [create-socket-controller]]
   [discord-gateway.message-parsers :refer [debug-msg]]
   [discord-gateway.utils :refer [basic-send print-debug parser-identity use-key]]
   [cljs.core.async :as a :refer [<! >! alt! chan close! go go-loop poll!]]))

(defn send-command [socket-control cmd]
  (let [command-c (:command-c @(:state socket-control))]
    (go (>! command-c cmd))))

(defn launch-gateway [{:keys [token guild-id channel-id]} & {:as opts}]
  (let [socket-controls (create-socket-controller opts)
        join-adder (use-key :gateway-handling
                            (first (create-handler-adder
                                    (create-gateway-handler
                                     "READY"
                                     #(do (basic-send (join-voice-msg guild-id channel-id)))))))]
    (send-command socket-controls join-adder)
    (send-command socket-controls (basic-send (create-identify token)))
    socket-controls))

(defn ^:export just-get-me-the-emojis-fam [js-socket-remote f]
  (let [command-c (:command_c (js->clj js-socket-remote :keywordize-keys true))
        adder (first (create-handler-adder
                      (create-gateway-handler
                       "VOICE_CHANNEL_EFFECT_SEND"
                       (fn [msg] #(do (f (clj->js (extract-emoji-info msg))) %)))))]
    (go (>! command-c (use-key :gateway-handling adder)))))

(defn ^:export js-launch-gateway [token guild-id channel-id & [debug]]
  (let [launch-args {:token token
                     :guild-id guild-id
                     :channel-id channel-id}
        socket-controls (launch-gateway launch-args :debug debug)
        {:keys [state close-f command-c]} socket-controls
        socket-remote {:read_state #(clj->js @state)
                       :close_socket close-f
                       :command_c command-c}]
    (clj->js socket-remote)))
