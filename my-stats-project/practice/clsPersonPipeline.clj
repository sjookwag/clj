(def users
  [{:name "Kim" :age 20}
   {:name "Lee" :age 30}
   {:name "Park" :age 40}])

(println (->> users
     (filter #(> (:age %) 25))
     (map :name)))

