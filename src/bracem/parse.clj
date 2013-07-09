(ns bracem.parse
  (:require [net.cgrand.parsley :as p]))

(def document (p/parser { :main [:ws? :body :ws?] }
 :body     #{ [:body :ws? :prose] [:body :ws? :form] :form :prose }
 :prose    [#"([^:}{\s])" #"(\\[}{]|[^}{])*"]
 :form     ["{" :ws? :word :ws? :attr* :body? :ws? "}"]
 :attr     [:keyword :ws :word :ws]
 :keyword  [":" :word]
 :word     #{ [#"[^\s:\"]" #"[^\s\"]*"] ["\"" #"[^\s:]" #"[^\"]*" "\""] }
 :ws       #"\s+"
 ))

(defn collapse-whitespace [s]
  (clojure.string/trim (clojure.string/replace s #"\s+" " ")))

(defmulti normalize :tag)

(defmethod normalize ::net.cgrand.parsley/root [tree]
  (mapcat normalize (:content tree)))

(defmethod normalize ::net.cgrand.parsley/unexpected [tree]
  [[:unexpected (first (:content tree))]])

(defmethod normalize ::net.cgrand.parsley/unfinished [tree]
  [[:unfinished (mapcat normalize (:content tree))]])

(defmethod normalize :body [tree]
  (mapcat normalize (:content tree)))

(defmethod normalize :prose [tree]
  (list (clojure.string/replace (apply str (:content tree)) #"\\([}{])" "$1")))

(defmethod normalize :form [tree]
  (let [children (filter identity (mapcat normalize (:content tree)))
        attr (filter #(= :attr (first %)) (rest children))
        other (filter #(not (= :attr (first %))) (rest children))]
    [(cons (first children) (cons (apply hash-map (mapcat rest attr)) other))]))

(defmethod normalize :attr [tree]
  [(concat (list :attr)
           (normalize (first (:content tree)))
           (normalize (nth (:content tree) 2)))])

(defmethod normalize :keyword [tree]
  (normalize (second (:content tree))))

(defmethod normalize :word [tree]
  (let [c (:content tree)]
    (if (= 2 (count c))
      (list (apply str (:content tree)))
      (list (apply str (take 2 (rest (:content tree))))))))

(defmethod normalize :ws [tree] nil)
(defmethod normalize nil [tree] nil)

(defn parse [text]
  (normalize (document text)))
