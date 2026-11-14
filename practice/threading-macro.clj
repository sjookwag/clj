(->> (range 1 11)
     (filter odd?)
     (map #(* % %))
     (reduce +)
     (println "1.")
)

(def sum
  (reduce +
    (map #(* % %)
      (filter odd? (range 1 11))
    )
  )
)
(println "2." sum)

(def sum (reduce + (map #(* % %) 
(filter odd? (range 1 11)))))
(println "3." sum)
