(defn average [nums]
  (/ (reduce + nums)
     (count nums)))
(println (average [10 20 30 40]))
