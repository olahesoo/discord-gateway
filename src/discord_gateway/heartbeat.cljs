(ns discord-gateway.heartbeat
  (:require
   [cljs.core.async :refer [<! >! alt! chan go go-loop timeout]]))

(def heartbeat {:op 1 :d nil})

(defn send-beat [{:keys [s send-c] :as state}]
  (go (>! send-c (assoc heartbeat :d s)))
  state)

(defn beat [command-c timeout-ms]
  (let [shutdown (chan)]
    (go (<! (timeout (rand timeout-ms)))
        (loop []
          (alt!
            [[command-c send-beat]] (do (<! (timeout timeout-ms)) (recur))
            shutdown nil)))
    #(go (>! shutdown :close))))

(defn close-beater-if-active [{:keys [close-beater] :as state}]
  (if close-beater
    (do (close-beater)
        (assoc state :close-beater nil))
    state))

(defn start-beater [timeout]
  (comp
   (fn [{:keys [command-c] :as state}]
     (let [close-beater (beat (:command-c state) timeout)]
       (assoc state :close-beater close-beater)))
   close-beater-if-active))
