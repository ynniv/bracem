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

(def apply-str #(apply str %))

(def escaped-characters
  {\\ \\, \/ \/, \b \backspace, \f \formfeed, \n \newline, \r \return,
   \t \tab})

(def normal-escape-sequence
  (p/semantics (p/lit-alt-seq (keys escaped-characters) nb-char-lit)
    escaped-characters))

(def escape-sequence
  (p/complex [_ escape-indicator
             character (p/alt ;unicode-char-sequence
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

(def open-brace    (nb-char-lit \{))
(def close-brace   (nb-char-lit \}))
(def colon         (nb-char-lit \:))

(def word-char     (comp nb-char (p/except p/anything
                                       (p/alt space tab line-break string-delimiter
                                              open-brace close-brace))))

(def nonkey-word   (p/semantics (p/conc (p/except word-char colon)
                                        (p/rep* word-char))
                                #(apply-str (apply cons %))))

(def key-word      (p/semantics (p/conc (nb-char-lit \:) (p/rep* word-char))
                                #(keyword (apply-str (second %)))))

(def value         (p/alt nonkey-word
                          string-lit))

(def attribute     (p/complex [kw    key-word
                               _     (p/opt ws)
                               value value
                               _     (p/opt ws)]
                              (list kw value)))

(declare run)
(def form          (p/complex [_     open-brace
                               _     (p/opt ws)
                               word  (p/opt nonkey-word)
                               _     (p/opt ws)
                               attrs (p/rep* attribute)
                               _     (p/opt ws)
                               body  (p/rep* (p/alt form))
                               _     (p/opt ws)
                               _     close-brace
                               ]
                              (concat (list (symbol word)
                                            (apply hash-map (apply concat attrs)))
                                      body)
                              ))

(def run           (p/semantics (p/rep* (p/alt word-char
                                               (p/constant-semantics ws \space)))
                                apply-str))

(def document      (p/rep* (p/alt form run)))
