# bracem

Lispy markup syntax

## Usage

lein run filename.br [renderer] [transformer] > filename.out

## License

Copyright (C) 2013 Vinny Fiano

Distributed under the Eclipse Public License, the same as Clojure.

## Syntax

    body     = body ws? prose | body ws? form | form | prose
    prose    = #"([^:}{\s])" #"(\\[}{]|[^}{])*"
    form     = "{" ws? word ws? attr* body? ws? "}"
    attr     = keyword ws word ws
    keyword  = ":" word
    word     = #"[^\s:\"]" #"[^\s\"]*" | "\"" #"[^\s:]" #"[^\"]*" "\""
    ws       = #"\s+"
