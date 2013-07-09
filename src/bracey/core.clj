(ns bracey.core
  (:gen-class)
  (:require [bracey.parse :as parse]))

(declare render-sgml)

(defn render-sgml-node [node]
  (if (string? node) node
      (let [[name attrs & bodies] node]
        (concat
         (list "<" name)
         (for [attr attrs] (str " " (first attr) "=\"" (second attr) "\""))
         (list ">")
         (render-sgml bodies)
         (list "</" name ">")))))

(defn render-sgml [list]
  (apply str (mapcat render-sgml-node list)))

(def transformers { "default" (fn [tree] tree) })
(def renderers    { "sgml"    render-sgml
                    "default" (fn [tree] (with-out-str (print tree))) })

(defn process-stream [stream renderer transformer]
  (renderer (map transformer (parse/parse stream))))

(defn do-main [filename & [renderer transformer]]
  (process-stream (slurp filename)
                  (or (get renderers renderer) (get renderers "default"))
                  (or (get transformers transformer) (get transformers "default"))))

(defn -main [& args]
  (if (> 1 (count args))
    (println "usage: bracem filename [renderer] [transformer]")
    (println (apply do-main args))))
