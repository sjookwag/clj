(defn make-multiplier [n]
  (fn [x]
    (* x n)))
(def multipleDBL (make-multiplier 2))
(def multipleTPL (make-multiplier 3))

(println (multipleDBL 10))
;; 20

(println (multipleTPL 10))
;; 30
