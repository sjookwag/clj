(def users
  [{:name "Kim" :age 25}
   {:name "Lee" :age 17}
   {:name "Park" :age 31}
   {:name "Choi" :age 15}])

(println "names who are older than 20:\n"
  (->> users
     (filter #(>= (:age %) 20))
     (map :name))
)

(println "names older than 20:\n"
  (map :name
     (filter #(>= (:age %) 20) users))
)
