(def students
  [
    {:name "Kim"  :score 85}
    {:name "Lee"  :score 95}
    {:name "Park" :score 70}
    {:name "Choi" :score 100}
  ]
)

(println "Students scoring ≥90:\n"
  (map :name (filter #(>= (:score %) 90) students))
)

(println "Name of Students scoring ≥90:\n"
  (->> students
     (filter #(>= (:score %) 90))
     (map :name)
  )
)

(println "Students:\n"
  (map :name students)
)

(def avg
  (/ (reduce + (map :score students))
     (count students)
  )
)

(println "The average:\n" 
  avg
)

(println "The best score:\n" 
  (apply max (map :score students))
)

(println "Top-scoring student:\n" 
  (apply max-key :score students)
)

(let 
  [
    avg 
    (/ (reduce + (map :score students))
       (count students)
    )
  ]
)

(println "Students scoring above average:\n"  
  (->> students
    (filter #(>= (:score %) avg))
    (map :name)
  )
)

(println "Pass/Fail Calssification:\n" 
  (group-by #(>= (:score %) 80) students)
)
