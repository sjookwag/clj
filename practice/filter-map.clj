(println "even x 10:\n"
  (->> [1 2 3 4 5 6]
     (filter even?)
     (map #(* 10 %)))
)

(println "another way:\n"
  (map #(* 10 %) (filter even? [1 2 3 4 5 6]))
)
