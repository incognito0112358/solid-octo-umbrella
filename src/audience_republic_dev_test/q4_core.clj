(ns audience-republic-dev-test.q4-core
  (:require [com.fulcrologic.guardrails.core :refer [>defn | =>]]
            [audience-republic-dev-test.q3-core :refer [shortest-path-with-dist]]
            [clojure.spec.alpha :as spec]))

(>defn eccentricity
  "The eccentricity of a vertex `src` is defined as the greatest distance between src and any other vertex.
  Returns `nil` the graph only has a single vertex."
  [graph src]
  [map? any? | #(and (not (empty? graph))
                     (contains? graph src)) => (spec/or :result int?
                                                        :none nil?)]
  (let [others (remove #{src} (keys graph))]
    (->> others
         (map #(shortest-path-with-dist graph src %))
         (filter (comp not nil?))
         (map second)
         ((fn [es]
            (when (not-empty es) (apply max es)))))))

(defn ^:private measure
  "Given the eccentricities of a graph's vertices, calculates some value
  on them via the application of function f."
  [graph f]
  (->> (keys graph)
       (map #(eccentricity graph %))
       (filter (comp not nil?))
       ((fn [es]
          (when (not-empty es) (apply f es))))))

(>defn radius
  "The radius of a graph is the minimum eccentricity of any vertex in a graph.
  Returns `nil` the graph only has a single vertex."
  [graph]
  [map? | #(not (empty? graph)) => (spec/or :result int?
                                            :none nil?)]
  (measure graph min))

(>defn diameter
  "The diameter of a graph is the maximum eccentricity of any vertex in a graph.
  Returns `nil` the graph only has a single vertex."
  [graph]
  [map? | #(not (empty? graph)) => (spec/or :result int?
                                            :none nil?)]
  (measure graph max))
