(set-env!
  :dependencies '[[org.clojure/clojure       "1.6.0"     :scope "provided"]
                  [boot/core                 "2.0.0-rc1" :scope "provided"]
                  [adzerk/bootlaces          "0.1.8"     :scope "test"]
                  [alandipert/jasmin         "2.4.0"]
                  [alandipert/boot-trinkets  "1.0.0"]]
  :source-paths #{"src"})

(require '[adzerk.bootlaces :refer :all]
         '[alandipert.boot-jasmin :refer [jasmin]]
         '[alandipert.boot-trinkets :refer [run]])

(def +version+ "2.4.0-0")

(bootlaces! +version+)

(task-options!
 pom  {:project     'alandipert/boot-jasmin
       :version     +version+
       :description "Boot task to assemble Jasmin files."
       :url         "https://github.com/alandipert/boot-jasmin"
       :scm         {:url "https://github.com/alandipert/boot-jasmin"}
       :license     {:name "Eclipse Public License"
                     :url  "http://www.eclipse.org/legal/epl-v10.html"}})
