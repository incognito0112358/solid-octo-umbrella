(ns audience-republic-dev-test.q3-core
  (:require [clojure.spec.alpha :as spec]
            [com.fulcrologic.guardrails.core :refer [>defn | =>]]))

(>defn path-from-hops
  "Starting from dest, this fn looks for a (reverse) path
  from dest -> src.
  Returns `nil` if there's no path, otherwise return [path dist]."
  [hops src dest]
  [map? any? any? => (spec/or :path sequential?
                              :no-path nil?)]
  (loop [[curr weight] [dest 0]
         path-with-len ['() 0]]
    (let [updated-path-with-len (-> path-with-len
                                    (update 0 conj curr)
                                    (update 1 + weight))
          next (hops curr)]
      (cond
        ;; Terminate with a path from src -> dest found
        (= curr src) updated-path-with-len
        ;; No path from src -> dest
        (nil? next) nil
        ;; Otherwise, keep going
        :else (recur next updated-path-with-len)))))

(>defn shortest-path-with-dist
  "Just like `shortest-path`, but returns [path dist].
  Can be used to calculate other graph properties such as
  eccentricity, radius, etc."
  [graph src dest]
  [map? any? any? |
   #(and (contains? graph src)
         (contains? graph dest)) => (spec/or :path (spec/tuple sequential? int?)
                                             :no-path nil?)]
  (loop [curr src
         ;; All initial distances are infinity except for the start vertex
         distances (merge (->> (keys graph)
                               (map #(vector % ##Inf))
                               (flatten)
                               (apply hash-map))
                          {src 0})
         hops {}
         unvisited (set (keys graph))]
    (if (or (empty? unvisited)
            (nil? curr)
            (= curr dest))
      (path-from-hops hops src dest) ; Base case
      (let [new-unvisited (set (remove #{curr} unvisited))
            ;; We don't care about the visited neighbors
            weighted-neighbors (->> (graph curr)
                                    (filter #(contains? new-unvisited (first %))))
            ;; This is the length of path: src -> curr -> neighbor
            transitive-path-len (fn [[_ weight]]
                                  (+ weight (distances curr)))
            [new-distances
             new-hops] (->> weighted-neighbors
                            (reduce (fn [[d h] wn]
                                      (let [len (transitive-path-len wn)
                                            [neighbor weight] wn]
                                        (if (< len (d neighbor))
                                          ;; Shorter path found, update
                                          ;; distances and hops accordingly
                                          [(assoc d neighbor len)
                                           (assoc h neighbor [curr weight])]
                                          [d h])))
                                    [distances hops]))]
        (recur
         ;; Next iteration's curr vertex is the one with min dist from src.
         (when (not-empty new-unvisited)
           (apply min-key #(new-distances %) new-unvisited))
         new-distances
         new-hops
         new-unvisited)))))

(>defn shortest-path
  "Using Dijkstra's algorithm, calculates the shortest path between
  2 selected vertices.

  Takes 3 args: the graph, src and dest vertices

  Returns `nil` if there's no path from src -> dest, otherwise returns the path"
  [graph src dest]
  [map? any? any? | #(and (contains? graph src)
                          (contains? graph dest)) => (spec/or :path sequential?
                                                              :no-path nil?)]
  (first (shortest-path-with-dist graph src dest)))
