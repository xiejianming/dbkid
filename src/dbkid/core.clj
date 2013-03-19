; Copyright (c) xjm (xiejianming@gmail.com). All rights reserved.
; Distributed under the Eclipse Public License, the same as Clojure.

(ns dbkid.core)

(defn now
  "Get current time in format <yyyy-MM-dd HH:mm:ss>."
  []
  (.format 
    (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm:ss")
    (.getTime (java.util.Calendar/getInstance))))

(def ^:dynamic *debug-flag* (atom true))

(defn db 
  "Disable/Enable DBK which used to print debug info."
  [& _] 
  (reset! *debug-flag* (not @*debug-flag*)) 
  (println (str "DBK is now " (if @*debug-flag* "Enabled!" "Disabled!"))))

(def ^:dynamic *debug-print-length* (atom 8))

(defn set-print-length [^long l] (reset! *debug-print-length* l))

(defmacro dbk
  "Print debug info(universal/global debugger)."
  [& variables]      
  `(if (deref *debug-flag*)
    (let [datetime# ~(now)
          naked-msg# (str "Debug: " datetime# " in " ~*file* "@" ~(:line (meta &form)))]
      (if (empty? '~variables)
        (println naked-msg#)
        (binding [*print-length* (deref *debug-print-length*)]
           (let [kvs# (into {} (map vec (partition 2 (interleave 
                                                       (map #(str % " =>") '~variables)
                                                       (vector ~@variables)))))]
             (println (str naked-msg# ":") kvs#)))))))

(def ^:dynamic *breakpoint-flag* (atom true))

(defn bp 
  "Disable/Enable break-points."
  [& _] 
  (reset! *breakpoint-flag* (not @*breakpoint-flag*)) 
  (println (str "Break points are now "
                (if @*breakpoint-flag* "Enabled!" "Disabled!"))))

(defn- eeval 
  [expr]
  (let [tp (type expr)]
    (cond
      (= tp clojure.lang.Compiler$NumberExpr) (.eval expr)
      (= tp clojure.lang.Compiler$DefExpr) (eval (.var expr))
      (= tp clojure.lang.Compiler$StringExpr) (.eval expr)
      (= tp clojure.lang.Compiler$InvokeExpr) (let [f (.eval (.fexpr expr))
                                                    argvs (map #(.eval %) (.args expr))]
                                                (apply f argvs))
      (= tp clojure.lang.Compiler$VectorExpr) (.eval expr)
      (= tp clojure.lang.Compiler$FnExpr) "#local_fn#"
      (= tp clojure.lang.Compiler$MapExpr) (.eval expr))))

(defmacro ?
  [& forms]
  (if (deref *breakpoint-flag*)
    (binding [*print-length* (deref *debug-print-length*)]
      (let [prompt #(do (print ">: ") (flush) (read-line))]
        (loop [cmd (prompt)]
          (if (or (= cmd "q") (= cmd ""))
            (rest &form)
            (do
              (case cmd
                "?" (println (str *file* "@" (:line (meta &form))))
                "l" (println (vec (sort (keys &env))))
                "ll" (println (vec (sort (keys (ns-interns *ns*)))))
                (let [s (read-string cmd) lb (if &env (&env s) nil)]
                  (print (str cmd ": "))
                  (try
                    (if lb
                      (println (eeval (.init lb)))
                      (println (eval s)))
                    (catch Exception e
                      (println (.getMessage e))))))
              (recur (prompt)))))))
    (rest &form)))