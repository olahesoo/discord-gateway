(ns discord-gateway.utils
  (:require
   [cljs.core.async :refer [go >!]]))

(defn pipe [x & fs] ((apply comp (reverse fs)) x))

(defn parser-identity [_] identity)

(defn use-key [key task]
  (fn [state]
    (let [next-state (task (key state))]
      (assoc state key next-state))))

(defn print-debug [content]
  (fn [state]
    (println "DEBUG: " content)
    state))

(defn basic-send [content]
  (fn [{:keys [send-c] :as state}]
    (go (>! send-c content))
    state))
