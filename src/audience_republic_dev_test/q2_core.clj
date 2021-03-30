(ns audience-republic-dev-test.q2-core
  (:require [com.fulcrologic.guardrails.core :refer [>defn | =>]]
            [clojure.data.generators :as gen]))

(defn ^:private rand-int-pure
  [n rng]
  (mod (rng) n))

(defn ^:private rand-nth-pure
  [coll rng]
  (nth coll
       (mod (rng) (count coll))))

(defn ^:private conjv
  "Like `conj` but generates a vector
  instead of a list if coll is nil."
  [coll x]
  (if (nil? coll) [] (conj coll x)))

(defn ^:private make-graph-first-pass
  "The first pass is simply for randomly generating
  all the vertices needed and an edge for every such pair."
  [vertex-count edge-count rng]
  (->> (range vertex-count)
       (reduce (fn [[graph s] n]
                 (let [new-vertex (-> n str keyword)
                       new-graph
                       (if (empty? graph)
                         ;; Make the first node
                         (assoc graph new-vertex [])
                         ;; Or
                         ;; - Make a new node
                         ;; - Take a node from the graph (so far)
                         ;; - Connect the 2 nodes
                         (let [rand-vertex (-> graph keys
                                               (rand-nth-pure rng))]
                           (-> graph
                               (assoc new-vertex [])
                               (update rand-vertex conjv
                                       [new-vertex (rand-int-pure 8 rng)]))))]
                   [new-graph (dec s)]))
               [{} edge-count])
       (first)))

(defn ^:private make-graph-second-pass
  "The second pass is simply for randomly generating
  more edges to meet the sparseness specified."
  [graph edge-count rng]
  (reduce
   (fn [g _]
     (let [;; By prioritizing the vertices with the
           ;; minimum neighbors, we guarantee that
           ;; we don't get a vertex with no valid
           ;; neighbor candidates
           src (->> g
                    (sort-by #(count (second %)))
                    (map first)
                    (first))
           src-neighbors (map first (g src))
           ;; Valid candidates are nodes that are:
           ;; - not the src vertex
           ;; - not already a neighbor of the src vertex
           dest (-> (conjv src-neighbors src)
                    (set)
                    (remove (keys g))
                    (rand-nth-pure rng))]
       (update g src conjv [dest (rand-int-pure 8 rng)])))
   graph
   (range edge-count)))

(>defn make-graph-pure
  "Pure equivalent of `make-graph`.
  Primary purpose is to allow deterministic behavior for testing
  Additional args:
  * rng - a fn that takes 0 args and returns an int
  "
  [n s rng]
  [int? int? fn? => map?]
  (-> (make-graph-first-pass n s rng)
      (make-graph-second-pass (- s (dec n)) rng)))

(>defn make-graph
  "Randomly generates an acyclic, weighted, connected graph.
  Takes 2 args:
  * n - Number of vertices in the graph
  * s - Sparseness (i.e. the number of edges) of the graph.
        Valid values: (n-1) inclusive - n(n - 1) inclusive
  Notes:
  * After the first pass, required edges needed is assumed to be to be `s - (n - 1)`
    because in the first pass, every vertex, upon generation, is attached to the tree
    so far (except for the first vertex).
  "
  [n s]
  [int? int? | #(and (pos-int? n)
                     (int? s) (>= s 0)
                     (>= s (dec n))
                     (<= s (* n (dec n)))) => map?]
  (make-graph-pure n s #(rand-int Integer/MAX_VALUE)))
