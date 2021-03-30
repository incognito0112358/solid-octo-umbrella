(ns audience-republic-dev-test.core
  (:require [clojure.string :refer [join]]))

(defn show-preamble
  []
  (doseq [line ["" "" ""
                "--------------------------------------------------------------------------"
                ""
                "Welcome to Rafael Nicdao's solutions for"
                "https://bitbucket.org/audiencerepublic/developer-test/wiki/clojure-2"
                ""

                "--- Notes ---"
                ""
                "--  General (i.e. applicable to all questions)  --"
                "* Null graphs (i.e. graphs with no vertices) are disallowed."
                " * While null graphs are valid graphs, handling them decreases readability of the code."
                " * I'll be glad to extend code if handling this case is a requirement."
                "* For the questions that require inputting a vertex, the respective solutions require"
                "  you to input a vertex that actually exists in the graph (verified via clojure.spec)."
                "* There are a bunch of unit tests in the project. You can run them via `lein test`."
                ""
                "--  Question 2  --"
                "* Max random edge weight is 8 (no special reason, I just happened to pick it)."
                ""
                "--  Question 3  --"
                "* If there is no path found, `nil` is returned."
                "* If there are multiple paths with the shortest length, only one is returned."
                " * I'll be glad to extend the code to return them all if this is a requirement."
                ""
                "--------------------------------------------------------------------------"
                "" "" ""]]
    (println line)))
