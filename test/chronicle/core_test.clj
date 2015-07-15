(ns chronicle.core-test
  (:require [clojure.test :refer :all]
            (chronicle
              [core :as op]
              [util :as util])))

(deftest test-persistent-threshold
  (let [state (atom [])
        bufsize 6
        logfile (java.io.File/createTempFile "test" ".tmp")
        opts {:logfile logfile :threshold 3}
        flush-fn (fn [vs] (reset! state vs))
        log (op/->threshold-persistent-log bufsize opts flush-fn)]

    (op/append log 1)
    (op/append log 2)

    (is (= 0 (.length logfile)))
    (is (empty? @state))

    (op/append log 3) ; Checkpoint threshold reached
    (is (= [1 2 3] (util/read-nippy logfile)))
    (is (empty? @state))

    (op/append log 4)
    (op/append log 5)
    (op/append log 6) ; Checkpoint threshold AND flush threshold reached

    (is (= 0 (.length logfile)))
    (is (= [1 2 3 4 5 6] @state))))
