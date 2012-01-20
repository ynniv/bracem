(ns bracey.watcher
  (:import [name.pachler.nio.file StandardWatchEventKind Paths FileSystems]))

(def *ws* (-> (FileSystems/getDefault) (.newWatchService)))

(def DEFAULT-EVENT-KINDS
  [StandardWatchEventKind/ENTRY_CREATE
   StandardWatchEventKind/ENTRY_MODIFY
   StandardWatchEventKind/ENTRY_DELETE])

(defn watch-path [path fn]
  (let [key (.register (Paths/get path) *ws* (into-array DEFAULT-EVENT-KINDS))]
    ))
