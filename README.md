# bracem

Lispy markup syntax

## Usage

lein run filename.br [renderer] [transformer] > filename.out

## License

Copyright (C) 2013 Vinny Fiano

Distributed under the Eclipse Public License, the same as Clojure.

## Example

    { h1 bracem }
    { p bracem is a markup language based on braces and keywords 
        that looks a lot like lisp. The important rules are that 
        \{ braces \} define forms, each form starts with a word, 
        :keyword "value" pairs follow, and the body can contain 
        a mixture of text and forms.}

## Syntax

    body     = body ws? prose | body ws? form | form | prose
    prose    = #"([^:}{\s])" #"(\\[}{]|[^}{])*"
    form     = "{" ws? word ws? attr* body? ws? "}"
    attr     = keyword ws word ws
    keyword  = ":" word
    word     = #"[^\s:\"]" #"[^\s\"]*" | "\"" #"[^\s:]" #"[^\"]*" "\""
    ws       = #"\s+"
