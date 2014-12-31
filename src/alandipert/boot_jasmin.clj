(ns alandipert.boot-jasmin
  {:boot/export-tasks true}
  (:require [boot.pod                 :as pod]
            [boot.core                :as core :refer [deftask]]
            [boot.util                :as util]
            [alandipert.boot-trinkets :refer [without-exiting]]))

(deftask jasmin
  "Assemble Jasmin source into class files."
  []
  (let [tgt         (core/temp-dir!)
        compile-pod (future (pod/make-pod (core/get-env)))]
    (fn [next-handler]
      (fn [fileset]
        (let [jasmin-files  (->> fileset
                                 core/input-files
                                 (core/by-ext [".j"]))
              jasmin-argv   (->> (map #(.getPath (core/tmpfile %)) jasmin-files)
                                 (list* "-d" (.getPath tgt))
                                 vec)]
          (core/empty-dir! tgt)
          (util/info "Assembling Jasmin sources...\n")
          (when (without-exiting
                 (pod/with-eval-in @compile-pod
                   (jasmin.Main/main (into-array ~jasmin-argv)))
                 true)
            (-> fileset
                (core/add-asset tgt)
                (core/rm jasmin-files)
                core/commit!
                next-handler)))))))
