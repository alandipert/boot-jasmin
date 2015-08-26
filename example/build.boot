(set-env!
 :source-paths #{"src"}
 :dependencies '[[alandipert/boot-jasmin "2.4.0-1" :scope "test"]
                 [alandipert/boot-trinkets "2.0.0" :scope "test"]])

(require '[alandipert.boot-jasmin :refer [jasmin]]
         '[alandipert.boot-trinkets :refer [run]])
