(defn fact [n]
  (loop [i n acc 1]
    (if (zero? i) 
      acc (recur (dec i) (* acc i))
    )
  )
)
(println (fact 5))  ;=> 120
