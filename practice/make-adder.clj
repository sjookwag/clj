(defn make-adder [x]
  (fn [y]
    (+ x y)))
(def add10 (make-adder 10))

(println (add10 5))

(def add100 (make-adder 100))
(def add1000 (make-adder 1000))

(println (add100 5))
;; 105

(println (add1000 5))
;; 1005
