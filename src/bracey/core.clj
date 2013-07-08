(ns bracey.core
  (:require [bracey.parse :as parse]))

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
                    "default" (fn [tree] (with-out-str (pprint tree))) })

(defn process-stream [stream renderer transformer]
  (renderer (map transformer (parse/parse stream))))

(defn do-main [filename & [renderer transformer]]
  (process-stream (slurp filename)
                  (or (get renderers renderer) (get renderers "default"))
                  (or (get transformers transformer) (get transformers "default"))))

(defn -main [& args]
  (print (apply do-main args)))
