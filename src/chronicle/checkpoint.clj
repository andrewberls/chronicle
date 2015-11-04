(ns chronicle.checkpoint
  "Helpers for persisting log contents to disk under varying conditions"
  (:require [framed.std.serialization :as serialization]))

(defn checkpoint
  "Write out the contents of a log to disk in its entirety

   TODO: only write out items enqueued since last checkpoint"
  [logfile log]
  (serialization/write-nippy logfile @log))

(defn counted-checkpoint
  "Record that an insert occurred by updating `counter`, and perform
   a synchronous checkpoint to `logfile` if a threshold is exceeded
   (resetting the counter)"
  [counter threshold logfile log]
  (swap! counter inc)
  (when (>= @counter threshold)
    (checkpoint logfile log)
    (reset! counter 0)))
