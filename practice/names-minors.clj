(def users
  [{:name "Kim" :age 25}
   {:name "Lee" :age 17}
   {:name "Park" :age 31}
   {:name "Choi" :age 15}])
(println "only names of minors:\n"
  (map :name (filter #(< (:age %) 20) users))
)
