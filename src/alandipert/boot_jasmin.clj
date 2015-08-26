(ns alandipert.boot-jasmin
  {:boot/export-tasks true}
  (:require [boot.pod  :as pod]
            [boot.core :as core :refer [deftask]]
            [boot.util :as util]))

(deftask jasmin
  "Assemble Jasmin source into class files."
  []
  (let [tgt         (core/tmp-dir!)
        compile-pod (future (pod/make-pod (core/get-env)))]
    (fn [next-handler]
      (fn [fileset]
        (let [jasmin-files  (->> fileset
                                 core/input-files
                                 (core/by-ext [".j"]))
              jasmin-argv   (->> (map #(.getPath (core/tmp-file %)) jasmin-files)
                                 (list* "-d" (.getPath tgt))
                                 vec)]
          (core/empty-dir! tgt)
          (util/info "Assembling Jasmin sources...\n")
          (when (util/without-exiting
                 (pod/with-eval-in @compile-pod
                   (jasmin.Main/main (into-array ~jasmin-argv)))
                 true)
            (-> fileset
                (core/add-asset tgt)
                (core/rm jasmin-files)
                core/commit!
                next-handler)))))))
