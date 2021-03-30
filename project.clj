(defproject audience-republic-dev-test "0.0.0"
  :description "Rafael Nicdao's solutions for
https://bitbucket.org/audiencerepublic/developer-test/wiki/clojure-2"
  :license {:name "EPL-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/data.generators "1.0.0"]
                 [org.clojure/core.async "1.3.610"]
                 [com.fulcrologic/guardrails "1.1.4"]]
  :main ^:skip-aot audience-republic-dev-test.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}
  :jvm-opts ["-Dguardrails.enabled"]
  :repl-options {:init-ns audience-republic-dev-test.core
                 :init (do (require '[audience-republic-dev-test.core :refer [show-preamble]]
                                    '[audience-republic-dev-test.q1-core
                                      :refer [traverse-graph-dfs
                                              seq-graph-dfs
                                              seq-graph-bfs]]
                                    '[audience-republic-dev-test.q2-core
                                      :refer [make-graph]]
                                    '[audience-republic-dev-test.q3-core
                                      :refer [shortest-path]]
                                    '[audience-republic-dev-test.q4-core
                                      :refer [eccentricity radius diameter]])
                           (show-preamble))})
