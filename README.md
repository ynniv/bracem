# bracem

Lispy markup syntax

## Usage

From source

    lein run filename.br [renderer] > filename.out

From jar

    java -jar filename.br [renderer] > filename.out

Current renderers

    default (simple stringify)
    sgml (names -> tags, attributes -> attributes, bodies -> children)

HTML example

    java -jar filename.br sgml > filename.html

## License

Copyright (C) 2013 Vinny Fiano

Distributed under the Eclipse Public License, the same as Clojure.

## Example

    { h1 bracem }
    { p :class intro :onclick "if (1) { alert('hello there!');}" bracem is a 
        markup language based on braces and keywords that looks a lot like 
        lisp. The important rules are that \{ braces \} denote forms, each 
        form starts with a word, :keyword "value" pairs follow, and the 
        remainder of the form can contain a mixture of text and forms. }

## Syntax

    body     = body ws? prose | body ws? form | form | prose
    prose    = #"([^:}{\s])" #"(\\[}{]|[^}{])*"
    form     = "{" ws? word ws? attr* body? ws? "}"
    attr     = keyword ws word ws
    keyword  = ":" word
    word     = #"[^\s:\"]" #"[^\s\"]*" | "\"" #"[^\s:]" #"[^\"]*" "\""
    ws       = #"\s+"
