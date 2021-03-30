(ns audience-republic-dev-test.q2-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.data.generators :as gen]
            [audience-republic-dev-test.utils :refer [spec-failure?]]
            [audience-republic-dev-test.q1-core :refer [seq-graph-bfs]]
            [audience-republic-dev-test.q2-core :refer [make-graph
                                                        make-graph-pure]]))

;; These tests rely on the fact that the `make-graph`fn is spec'd
;; to check for the required pre-conditions for the values of N and S

(defn ^:private connected?
  [graph]
  (let [vertices (keys graph)
        traversal (seq-graph-bfs graph (first vertices))]
    ;; If the graph is "connected", then all
    ;; vertices should be reachable
    (= (set traversal)
       (set vertices))))

(defn ^:private valid-graph?
  "Makes a graph (using the pure fn version `make-graph-pure`) then:
  * Checks if the specified vertex and edge counts are actually obeyed.
  * Checks if the graph is connected."
  [n s]
  (let [g (make-graph-pure n s gen/int)]
    (and (connected? g)
         (= n (count (keys g)))
         (= s (count (mapcat second g))))))

(deftest verify-make-graph-pure
  (testing "We expect `make-graph-pure` to be pure"
    ;; Well, this doesn't really tell us whether the
    ;; fn is pure but probably the best we can do
    (let [rng (constantly 123)]
      (is (= (make-graph-pure 5 10 rng)
             (make-graph-pure 5 10 rng)
             (make-graph-pure 5 10 rng)))
      (is (not= (make-graph-pure 5 10 (constantly 123))
                (make-graph-pure 5 10 (constantly 456)))))))

(deftest valid-graph
  (testing "A valid graph is generated with parameters with"

    (testing "equal number of vertices and edges"
      (is (valid-graph? 5 5)))

    (testing "max number of edges"
      (let [n 5
            s (* n (dec n))]
        (is (valid-graph? n s))))

    (testing "a single vertex"
      (is (valid-graph? 1 0)))

    (testing "no vertices and no edges (invalid input)"
      (is (spec-failure?
           (make-graph 0 0))))

    (testing "too few edges - thus, not connected (invalid input)"
      (is (spec-failure?
           (make-graph 5 1))))))
