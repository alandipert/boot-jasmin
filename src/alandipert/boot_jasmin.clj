(ns alandipert.boot-jasmin
  {:boot/export-tasks true}
  (:require [boot.pod        :as pod]
            [boot.core       :as core]
            [boot.util       :as util]
            [clojure.java.io :as io])
  (:refer-clojure :exclude [compile]))

;;; borrowed

(defmacro without-exiting
  "Evaluates body in a context where System/exit doesn't work.
  Returns result of evaluating body, or nil if code in body attempted to exit."
  [& body]
  `(let [old-sm# (System/getSecurityManager)
         new-sm# (proxy [SecurityManager] []
                   (checkPermission [p#])
                   (checkExit [s#] (throw (SecurityException.))))]
     (System/setSecurityManager ^SecurityManager new-sm#)
     (try ~@body
          (catch SecurityException e#)
          (finally (System/setSecurityManager old-sm#)))))

(defn- copy [tf dir]
  (let [f (core/tmpfile tf)]
    (io/copy f (doto (io/file dir (:path tf)) io/make-parents))))

(core/deftask run
  "Add classes to a pod and run a main method."
  [m main CLASSNAME sym   "The main class"
   a args ARGUMENTS [str] "String arguments to pass to the main class's main method"]
  (let [classdir (core/temp-dir!)
        runners  (pod/pod-pool (core/get-env))]
    (core/with-pre-wrap fileset
      (let [class-files (->> fileset
                             core/output-files
                             (core/by-ext [".class"]))
            main-sym    (symbol (str main) "main")]
        ;; TODO - copy efficiently with fileset diff
        (core/empty-dir! classdir)
        (doseq [tmpfile class-files] (copy tmpfile classdir))
        (pod/with-eval-in (runners :refresh)
          (boot.pod/add-classpath ~(.getPath classdir))
          (~main-sym (into-array String ~(vec args)))))
      fileset)))

(def ^:private env
  {:repositories [["jasmin-repo" "https://dl.dropboxusercontent.com/u/12379861/jasmin-repo/"]]
   :dependencies '[[jasmin "2.4.0"]]})

(core/deftask jasmin
  "Assemble Jasmin source into class files."
  []
  (let [tgt         (core/temp-dir!)
        env         (merge-with into (core/get-env) env)
        compile-pod (future (pod/make-pod env))]
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
