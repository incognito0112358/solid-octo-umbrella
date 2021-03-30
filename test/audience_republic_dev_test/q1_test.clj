(ns audience-republic-dev-test.q1-test
  (:require [clojure.test :refer [deftest testing is]]
            [audience-republic-dev-test.q1-core
             :refer [traverse-graph-dfs
                     seq-graph-dfs
                     seq-graph-bfs]]))

(deftest traverse-small-graph
  (let [g {:1 [[:2 1] [:3 2]]
           :2 [[:4 4]]
           :3 [[:4 2]]
           :4 []}]
    (testing "Traverse a small graph"
      (testing "via eager DFS"
        (is (= (traverse-graph-dfs g :1)
               [:1 :3 :4 :2])))
      (testing "via lazy DFS"
        (is (= (seq-graph-dfs g :1)
               [:1 :3 :4 :2])))
      (testing "via eager BFS"
        (is (= (seq-graph-bfs g :1)
               [:1 :2 :3 :4]))))))

(deftest traverse-bigger-graph
  (let [g {:1 [[:2 2] [:3 3] [:4 4] [:5 5] [:6 6]]
           :2 [[:3 3] [:4 4] [:5 5] [:6 6] [:1 1]]
           :3 [[:4 4] [:5 5] [:6 6] [:2 2] [:1 1]]
           :4 [[:5 5] [:6 6]]
           :5 [[:6 6]]
           :6 []}]
    (testing "Traverse a bigger, *more* connected graph"
      (testing "via eager DFS"
        (is (= (traverse-graph-dfs g :1)
               [:1 :6 :5 :4 :3 :2])))
      (testing "via lazy DFS"
        (is (= (seq-graph-dfs g :1)
               [:1 :6 :5 :4 :3 :2])))
      (testing "via eager BFS"
        (is (= (seq-graph-bfs g :1)
               [:1 :2 :3 :4 :5 :6]))))))

(deftest traverse-solitary-vertex
  (let [g {:1 []}]
    (testing "Traverse a graph with a single vertex"
      (testing "via eager DFS"
        (is (= (traverse-graph-dfs g :1)
               [:1])))
      (testing "via lazy DFS"
        (is (= (seq-graph-dfs g :1)
               [:1])))
      (testing "via eager BFS"
        (is (= (seq-graph-bfs g :1)
               [:1]))))))
