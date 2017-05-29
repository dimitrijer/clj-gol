(defproject clj-gol "0.1.0-SNAPSHOT"
  :description "Game of Life"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [seesaw "1.4.5"]
                 [overtone/at-at "1.2.0"]]
  :java-source-paths ["src/java"]
  :main ^:skip-aot clj-gol.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
