(set-env!
 :dependencies '[[adzerk/bootlaces         "0.1.11" :scope "test"]
                 [alandipert/jasmin        "2.4.0"]
                 [alandipert/boot-trinkets "1.0.0"]]
  :source-paths #{"src"})

(require '[adzerk.bootlaces :refer :all]
         '[alandipert.boot-jasmin :refer [jasmin]]
         '[alandipert.boot-trinkets :refer [run]])

(def +version+ "2.4.0-1")

(bootlaces! +version+)

(task-options!
 pom  {:project     'alandipert/boot-jasmin
       :version     +version+
       :description "Boot task to assemble Jasmin files."
       :url         "https://github.com/alandipert/boot-jasmin"
       :scm         {:url "https://github.com/alandipert/boot-jasmin"}
       :license     {"Eclipse Public License" "http://www.eclipse.org/legal/epl-v10.html"}})
