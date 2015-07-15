(ns chronicle.checkpoint
  "Helpers for persisting log contents to disk
   under varying conditions"
  (:require [clj-time.core :as tcore]
            [chronicle.util :as util]))

(defn- checkpoint
  "Write out the contents of a log to disk in its entirety;
   does *not* perform optimizations such as tracking to only
   write out items enqueued since last checkpoint"
  [logfile log]
  (util/write-nippy logfile @log))

(defn checkpoint-loop
  "Start a loop writing the contents of `log` atom to `logfile`
   every `interval`

   Loop is blocking, expected to be called from new thread context"
  [log logfile interval]
  (let [interval-ms (tcore/in-millis interval)]
    (loop []
      (Thread/sleep interval-ms)
      (checkpoint logfile log)
      (recur))))

(defn counted-checkpoint
  "Record that an insert occurred by updating `counter`, and perform
   a synchronous checkpoint to `logfile` if a threshold is exceeded
   (resetting the counter)"
  [counter threshold logfile log]
  (swap! counter inc)
  (when (>= @counter threshold)
    (checkpoint logfile log)
    (reset! counter 0)))
