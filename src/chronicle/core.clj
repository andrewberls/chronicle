(ns chronicle.core
  (:require [clojure.java.io :as io]
            [clj-time.core :as tcore]
            (framed.std
              [core :as std :refer [future-loop]]
              [serialization :as serialization])
            [chronicle.checkpoint :as cp]))

(defprotocol ILog
  (append [this v]))

;;

(defn log-atom
  "Construct a log atom, using the contents of `logfile`
   as an initial state if non-empty"
  [logfile]
  (let [initial-log (if (> (.length (io/file logfile)) 0)
                       (serialization/read-nippy logfile)
                       [])]
    (atom (vec initial-log))))

(defn ->threshold-persistent-log
  "Construct a log instance that buffers values and flushes once a certain
   bufsize is reached, and persists values to disk after a certain number
   of inserts occur

   opts - map of
     {:logfile <file-like to use for persistence>
      :threshold <Integer checkpoint insert threshold>}"
  [bufsize opts flush-fn]
  (let [{:keys [logfile threshold]} opts
        log (log-atom logfile)
        inserts-counter (atom 0)] ; Since last checkpoint
    (reify ILog
     (append [this v]
       (swap! log conj v)
       (cp/counted-checkpoint inserts-counter threshold logfile log)
       (when (>= (count @log) bufsize)
         (flush-fn @log)
         (reset! log [])
         (spit logfile ""))))))

(defn ->interval-persistent-log
  "Construct a log instance that buffers values and flushes once a certain
   bufsize is reached, and persists values to disk periodically in a separate
   thread based on a specified time interval

   opts - map of
     {:logfile <file-like to use for persistence>
      :interval <org.joda.time.ReadablePeriod>}

   Ex interval: (clj-time.core/seconds 30)"
  [bufsize opts flush-fn]
  (let [{:keys [logfile interval]} opts
        interval-ms (tcore/in-millis interval)
        log (log-atom logfile)]
    (future-loop
      (Thread/sleep interval-ms)
      (cp/checkpoint logfile log))
    (reify ILog
     (append [this v]
       (swap! log conj v)
       (when (>= (count @log) bufsize)
         (flush-fn @log)
         (reset! log [])
         (spit logfile ""))))))
