(ns bracey.parse
  (:require [name.choi.joshua.fnparse :as p]))

;; from sample json parser

(defn- nb-char [subrule] subrule #_(p/invisi-conc subrule (p/update-info :column inc)))
(defn- b-char  [subrule] subrule #_(p/invisi-conc subrule (p/update-info :line inc)))

(def nb-char-lit  (comp nb-char p/lit))
(def nb-char-lit2 (comp nb-char nb-char p/lit))
(def space        (nb-char-lit \space))
(def tab          (nb-char-lit \tab))
(def newline-lit  (p/lit \newline))
(def return-lit   (p/lit \return))
(def line-break   (b-char (p/rep+ (p/alt newline-lit return-lit))))
(def ws           (p/constant-semantics (p/rep* (p/alt space tab line-break)) :ws))

(def escape-indicator (nb-char-lit \\))
(def string-delimiter (nb-char-lit \"))

(def unescaped-char
  (p/except p/anything (p/alt escape-indicator string-delimiter)))

(def zero-digit (nb-char-lit \0))
(def nonzero-decimal-digit (p/lit-alt-seq "123456789" nb-char-lit))
(def decimal-digit (p/alt zero-digit nonzero-decimal-digit))
(def hexadecimal-digit (p/alt decimal-digit (p/lit-alt-seq "ABCDEF" nb-char-lit)))

(def unicode-char-sequence
  (p/complex [_ (nb-char-lit \u)
              digits (p/factor= 4
                       #_(failpoint hexadecimal-digit
                         (expectation-error-fn "hexadecimal digit")))]
    (-> digits #(apply str %) (Integer/parseInt 16) char)))

(def escaped-characters
  {\\ \\, \/ \/, \b \backspace, \f \formfeed, \n \newline, \r \return,
   \t \tab})

(def normal-escape-sequence
  (p/semantics (p/lit-alt-seq (keys escaped-characters) nb-char-lit)
    escaped-characters))

(def escape-sequence
  (p/complex [_ escape-indicator
             character (p/alt unicode-char-sequence
                              normal-escape-sequence)]
    character))

(def string-char
  (p/alt escape-sequence unescaped-char))

(def string-lit
  (p/complex [_ string-delimiter
              contents (p/rep* string-char)
              _ string-delimiter]
             contents))

;; /sample json parser

(def word-char     (comp nb-char (p/except p/anything
                           (p/alt (p/lit \space) (p/lit \tab) (p/lit \") (p/lit \:)
                                  (p/lit \() (p/lit \)) (p/lit \{) (p/lit \})))))
(def word          (p/rep+ word-char))
(def kw            (p/conc (nb-char-lit \:) word))
(def value         (p/alt word (p/conc (nb-char-lit \") string-lit (nb-char-lit \"))))
(def attribute     (p/conc kw ws value (p/opt ws)))
(def form          (p/conc (nb-char-lit \{) (p/opt ws)
                         word (p/opt ws)
                         (p/rep* attribute) (p/opt ws)
                         (p/opt string-lit) (p/opt ws)
                         (nb-char-lit \})))
(def document      (p/rep* (p/alt form (p/rep* string-char))))
