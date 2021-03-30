(ns audience-republic-dev-test.q3-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.data.generators :as gen]
            [audience-republic-dev-test.utils :refer [spec-failure?]]
            [audience-republic-dev-test.q2-core :refer [make-graph-pure]]
            [audience-republic-dev-test.q3-core :refer [shortest-path
                                                        path-from-hops]]))

(deftest tracing-path-from-hops
  (testing "Constructing a path from the hops"
    (let [h {:1 [:0 2]
             :2 [:0 4]
             :4 [:0 3]
             :3 [:1 5]
             :6 [:1 2]
             :7 [:4 6]
             :8 [:4 1]}]
      (is (= (path-from-hops h :0 :7)
             [[:0 :4 :7] 9]))
      (is (= (path-from-hops h :0 :6)
             [[:0 :1 :6] 4])))))

(deftest selecting-invalid-vertices
  (testing "Selecting non-existent vertices"
    (let [g {:1 [:2 2]
             :2 []}]
      (is (spec-failure?
           (shortest-path g :i-dont-exist :2)))
      (is (spec-failure?
           (shortest-path g :1 :i-dont-exist))))))

(deftest shortest-paths-dense-graph
  (testing "Finding shortest paths within a dense graph"
    (let [g {:0 [[:1 2] [:2 3]]
             :1 [[:4 4]]
             :2 [[:3 1] [:4 3]]
             :3 [[:4 1]]
             :4 []}]
      ;; Just the src vertex itself
      (is (= (shortest-path g :0 :0)
             [:0]))
      ;; Direct neighbor
      (is (= (shortest-path g :0 :1)
             [:0 :1]))
      ;; Transitive neighbor/s
      (is (= (shortest-path g :0 :3)
             [:0 :2 :3]))
      (is (= (shortest-path g :0 :4)
             [:0 :2 :3 :4]))
      ;; No path
      (is (= (shortest-path g :1 :2)
             nil)))))

(deftest shortest-paths-sparse-graph
  (testing "Finding shortest paths within a sparse graph"
    (let [g {:0 [[:1 2] [:2 4] [:4 3]]
             :1 [[:3 5] [:6 2]]
             :2 []
             :3 []
             :4 [[:6 1] [:7 6] [:8 1]]
             :6 []
             :5 [[:6 4]]
             :7 [[:8 3]]
             :8 []}]
      ;; Just the src vertex itself
      (is (= (shortest-path g :0 :0)
             [:0]))
      ;; Direct neighbor
      (is (= (shortest-path g :0 :1)
             [:0 :1]))
      ;; Transitive neighbor/s
      (is (= (shortest-path g :0 :3)
             [:0 :1 :3]))
      ;; Note: This path is equally short with [:0 :4 :6]
      ;; but this fn just returns 1 path. I could extend it
      ;; to return multiple shortest paths, if you want :^)
      (is (= (shortest-path g :0 :6)
             [:0 :1 :6]))
      ;; No path
      (is (= (shortest-path g :0 :5)
             nil))
      (is (= (shortest-path g :2 :8)
             nil))
      (is (= (shortest-path g :5 :8)
             nil))
      ;; Doesn't hurt to test more cases
      (is (= (shortest-path g :0 :2)
             [:0 :2]))
      (is (= (shortest-path g :0 :4)
             [:0 :4]))
      (is (= (shortest-path g :0 :7)
             [:0 :4 :7]))
      (is (= (shortest-path g :0 :8)
             [:0 :4 :8]))
      (is (= (shortest-path g :4 :7)
             [:4 :7]))
      (is (= (shortest-path g :4 :8)
             [:4 :8])))))

(deftest sanity-test-q2
  (testing "We want to make sure that \"random\" graphs from Q2 can have their shortest paths calculated"
    ;; Note that Q2's "random" graph generation can be deterministically done
    ;; via `clojure.data.generators`
    (for [_ (range 10)
          :let [g (make-graph-pure 5 15 gen/int)]]
      ;; We just want to check that no errors are thrown
      ;; when the shortest-path is calculated
      (is (any? (shortest-path g (first (keys g)) (last (keys g))))))))
