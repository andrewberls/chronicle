(ns chronicle.core-test
  (:require [clojure.test :refer :all]
            [framed.std.serialization :as serialization]
            [chronicle.core :as c]))

(deftest test-persistent-threshold
  (let [state (atom [])
        bufsize 6
        logfile (java.io.File/createTempFile "test" ".tmp")
        opts {:logfile logfile :threshold 3}
        flush-fn (fn [vs] (reset! state vs))
        log (c/->threshold-persistent-log bufsize opts flush-fn)]

    (c/append log 1)
    (c/append log 2)

    (is (= 0 (.length logfile)))
    (is (empty? @state))

    (c/append log 3) ; Checkpoint threshold reached
    (is (= [1 2 3] (serialization/read-nippy logfile)))
    (is (empty? @state))

    (c/append log 4)
    (c/append log 5)
    (c/append log 6) ; Checkpoint threshold AND flush threshold reached

    (is (= 0 (.length logfile)))
    (is (= [1 2 3 4 5 6] @state))))
