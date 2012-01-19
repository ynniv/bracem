(ns bracey.core
  (:require bracey.parse))

(defn render [x]
  (str x))

(def forms {})

(defn add-form [name form]
  (def forms (conj { name form } forms)))

(defn fn-for-form [name]
  (or (get forms name)
      (get forms 'basic)))

(defn first-attr-value [attrs key & [default]]
  (or (second (first (filter #(= (first %) key) attrs))) default))

(defn transform [x]
  (cond (seq? x)  (let [[name attrs & body] x
                        attrs (concat (partition 2 attrs) `((:form-name ~name)))
                        func (fn-for-form name)]
                    (func attrs body))
        (= x :ws) (list " ")
        :else     (list x)))
        
(defmacro form-alias [new old]
  `(add-form '~new (get forms '~old)))

(defmacro defform [name attrs body]
  `(add-form (quote ~name) (fn ~attrs ~body)))

(defform basic [attrs body]
  (let [attrs (or attrs {})
        tag (or (first-attr-value attrs :tag)
                (first-attr-value attrs :form-name))]
    (concat (if tag (list "<" tag ">"))
            (apply concat (map transform body))
            (if tag (list "</" tag ">")))))

(form-alias document basic)

(defform keyword [attrs body]
  nil)

(form-alias k keyword)

(defn process-stream [stream]
  (render (transform (bracey.parse/parse stream))))

(defn -main [filename]
  (print (process-stream (slurp filename))))
