(defproject com.andrewberls/chronicle "0.1.1"
  :description "Persistent logs for Clojure"
  :url "https://github.com/andrewberls/chronicle"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-time "0.9.0"]
                 [com.taoensso/nippy "2.8.0"]
                 [io.framed/std "0.1.2"]]
  :profiles {:uberjar {:aot :all}})
