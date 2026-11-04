(defn make-discount [rate]
  (fn [price]
    (* price (- 1 rate))))
(def vip-discount
  (make-discount 0.2))

(println "The discounted price:" (vip-discount 100))
