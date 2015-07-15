(ns chronicle.util
 (:require [clojure.java.io :as io]
           [taoensso.nippy :as nippy])
 (:import (java.io EOFException DataInputStream DataOutputStream)))

(defn data-input-stream [istream-like]
  (->> (io/input-stream istream-like)
       (DataInputStream.)))

(defn- data-output-stream [ostream-like]
  (->> (io/output-stream ostream-like)
       (DataOutputStream.)))

(defn read-nippy
  [istream-like]
  (let [istream (data-input-stream istream-like)]
    (when-let [obj (try (nippy/thaw-from-stream! istream)
                        (catch EOFException ex nil))]
      (cons obj (lazy-seq (read-nippy istream))))))

(defn write-nippy [ostream-like coll]
  (with-open [ostream (data-output-stream ostream-like)]
    (doseq [x coll]
      (nippy/freeze-to-stream! ostream x))
   ostream-like))
