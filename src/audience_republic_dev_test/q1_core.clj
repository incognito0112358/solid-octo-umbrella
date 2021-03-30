(ns audience-republic-dev-test.q1-core
  (:require [com.fulcrologic.guardrails.core :refer [>defn | =>]]))

(>defn traverse-graph-dfs
  "Traverses a graph depth-first."
  [g s]
  [map? any? | #(contains? g s) => sequential?]
  (loop [vertices [] explored #{s} frontier [s]]
    (if (empty? frontier)
      vertices
      (let [v (peek frontier)
            neighbors (map first (g v))]
        (recur
         (conj vertices v)
         (into explored neighbors)
         (into (pop frontier) (remove explored neighbors)))))))

(>defn ^:private seq-graph
  [d g s]
  [sequential? map? any? | #(contains? g s) => sequential?]
  ((fn rec-seq [explored frontier]
     (lazy-seq
      (if (empty? frontier)
        nil
        (let [v (peek frontier)
              neighbors (map first (g v))]
          (cons v (rec-seq
                   (into explored neighbors)
                   (into (pop frontier) (remove explored neighbors))))))))
   #{s} (conj d s)))

(def seq-graph-dfs
  "Lazily traverses a graph depth-first."
  (partial seq-graph []))

(def seq-graph-bfs
  "Lazily traverses a graph breadth-first."
  (partial seq-graph (clojure.lang.PersistentQueue/EMPTY)))
