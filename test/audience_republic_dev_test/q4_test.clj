(ns audience-republic-dev-test.q4-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.spec.alpha :as spec]
            [com.fulcrologic.guardrails.core :refer [>defn => |]]
            [audience-republic-dev-test.utils :refer [spec-failure?]]
            [audience-republic-dev-test.q4-core :refer [eccentricity
                                                        radius
                                                        diameter]]))

(deftest measurements
  (let [graph {:0 [[:1 2] [:2 4] [:4 3]]
               :1 [[:3 5] [:6 2]]
               :2 []
               :3 []
               :4 [[:6 1] [:7 6] [:8 1]]
               :6 []
               :5 [[:6 4]]
               :7 [[:8 3]]
               :8 []}]
    (testing "Calculate the eccentricity of a graph"
      (is (= (eccentricity graph :0) 9))
      (is (= (eccentricity graph :1) 5))
      (is (= (eccentricity graph :2) nil))
      (is (= (eccentricity graph :3) nil))
      (is (= (eccentricity graph :4) 6))
      (is (= (eccentricity graph :5) 4))
      (is (= (eccentricity graph :6) nil))
      (is (= (eccentricity graph :7) 3))
      (is (= (eccentricity graph :8) nil))
      (testing "but selecting a non-existent vertex"
        (is (spec-failure?
             (eccentricity graph :i-dont-exist)))))
    (testing "Calculate the radius of a graph"
      (is (= (radius graph) 3)))
    (testing "Calculate the diameter of a graph"
      (is (= (diameter graph) 9)))))

(deftest measurements-single-vertex
  (let [graph {:0 []}]
    (testing "Given a graph with a single vertex"
      (testing "calculate the eccentricity"
        (is (= (eccentricity graph :0) nil)))
      (testing "calculate the radius"
        (is (= (radius graph) nil)))
      (testing "calculate the diameter"
        (is (= (diameter graph) nil))))))
