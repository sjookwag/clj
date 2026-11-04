(def users
  [{:name "Kim" :age 20}
   {:name "Lee" :age 30}
   {:name "Park" :age 40}])
(println (filter #(> (:age %) 25) users))
(println (map :name (filter #(> (:age %) 25) users)))
