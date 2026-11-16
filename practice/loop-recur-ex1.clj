(loop [xs [1 2 3 4] total 0]
  (if (empty? xs)
    (println total)
    (recur (rest xs) (+ total (first xs)))
  )
)

(println
  (loop [xs [1 2 3 4] total 0]
    (if (empty? xs)
      total
      (recur (rest xs) (+ total (first xs)))
    )
  )
)
