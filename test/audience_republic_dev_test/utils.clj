(ns audience-republic-dev-test.utils
  (:require [clojure.string :refer [includes?]]))

;; Got the idea from https://clojureverse.org/t/testing-thrown-ex-info-exceptions/6146/5
(defmacro spec-failure?
  "(Rudimentarily) checks if executing body fails its specs"
  [body]
  `(try (do ~body
            ;; If control gets here, no error is thrown
            ;; = unsatisfied assertion
            false)
        (catch clojure.lang.ExceptionInfo e#
          (includes? (ex-message e#) "Spec failed"))))
