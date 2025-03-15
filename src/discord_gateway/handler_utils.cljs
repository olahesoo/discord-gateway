(ns discord-gateway.handler-utils
  (:require [discord-gateway.utils :refer [parser-identity use-key]]))

(defn dispatch [event & {:keys [accessor] :or {accessor identity}}]
  (fn [state]
    (let [handlers-location (accessor state)
          {:keys [handlers default-parser] :or {handlers [] default-parser parser-identity}} handlers-location
          matching-handlers (filter #((:match %) event) handlers)
          use-default (every? :keep-default matching-handlers)
          add-default-parser (if use-default #(conj % default-parser) identity)
          task (->> matching-handlers
                    (map :parse)
                    (add-default-parser)
                    (map #(% event))
                    (reduce comp))]
      (task state))))

(defn handler-exists [id handlers]
  (some #(= (:id %) id) handlers))

(defn create-handler-adder [handler]
  (let [id (gensym)]
    [(fn [{:keys [handlers] :or {handlers []} :as state}]
       (let [new-handlers (if (handler-exists id handlers)
                            handlers
                            (conj handlers (assoc handler :id id)))]
         (assoc state :handlers new-handlers)))
     id]))

(defn create-handler-remover [id]
  (fn [{:keys [handlers] :or {handlers []} :as state}]
    (let [new-handlers (filterv #(not= id (:id %)) handlers)]
      (assoc state :handlers new-handlers))))

(defn handler-changers [handler]
  (let [[handler-adder id] (create-handler-adder handler)
        handler-remover (create-handler-remover id)]
    [handler-adder handler-remover]))

(defn handlers-adder [handlers & {:keys [key default-parser]}]
  (fn [state]
    (let [handlers-adder-task (->> handlers
                                   (map create-handler-adder)
                                   (map first)
                                   (apply comp))
          default-adder-task (if default-parser
                               #(assoc % :default-parser default-parser)
                               identity)]
      (as-> (comp handlers-adder-task default-adder-task) x
        (if key (use-key key x) x)
        (x state)))))
