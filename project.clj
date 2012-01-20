(defproject bracey "1.0.0-SNAPSHOT"
  :description "Lispy markup language."
  :main bracey.core
  :repositories {"local" ~(str (.toURI (java.io.File. "maven_repository")))}
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [fnparse "2.2.7"]
                 [jpathwatch "0.95"]
                 ])
