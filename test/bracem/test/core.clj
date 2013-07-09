(ns bracem.test.core
  (:use [bracem.core])
  (:use [clojure.test]))

(deftest basic-smoke
  (is true (= "<h1>bracem </h1><p onclick=\"if (1) { alert('hello there!');}\" class=\"intro\">bracem is a markup language based on braces and keywords \n        that looks a lot like lisp. The important rules are that \n        { braces } define forms, each form starts with a word, \n        :keyword \"value\" pairs follow, and the body can contain \n        a mixture of text and forms. </p>"
              (process-stream "    { h1 bracem }\n    { p :class intro :onclick \"if (1) { alert('hello there!');}\"\n        bracem is a markup language based on braces and keywords \n        that looks a lot like lisp. The important rules are that \n        \\{ braces \\} define forms, each form starts with a word, \n        :keyword \"value\" pairs follow, and the body can contain \n        a mixture of text and forms. }\n" render-sgml identity))))

