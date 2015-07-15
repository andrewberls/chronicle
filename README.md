## Chronicle

Chronicle provides simple log types (acting like buffers / queues) for Clojure that can be persisted to disk, and periodically flush their contents based on
configurable thresholds. It is useful for avoiding expensive operations on hot code paths (such as sending data to external services), and instead
buffering items and aggregating/flushing at controlled periods in a manner that can tolerate process death.

### Installation
[![Clojars Project](http://clojars.org/com.andrewberls/chronicle/latest-version.svg)]()

### Usage
```clj
(require '[chronicle.core :as c])

(def bufsize 4) ; Items flushed after this many inserts
(def opts {:logfile "my-log.nippy" :threshold 2})
(defn flush-fn [vs] (println "Flushing values: " vs))
(def log (c/->threshold-persistent-log bufsize opts flush-fn))

(c/append log 1)
(c/append log 2) ; Checkpoint to disk triggered
(c/append log 3)
(c/append log 4) ; Checkpoint triggered then flush
; Flushing values:  [1 2 3 4]
```

Chronicle logs are persisted via [Nippy](https://github.com/ptaoussanis/nippy/), and so any value that can be serialized with Nippy can be used.

### Contributing
Please use the [GitHub issues page](https://github.com/andrewberls/chronicle/issues) for questions/comments/suggestions (pull requests welcome!).
You can also find me on [Twitter](https://twitter.com/aberls).
