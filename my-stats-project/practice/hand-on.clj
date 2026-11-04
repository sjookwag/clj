(def users
  [{:name "Kim" :age 20}
   {:name "Lee" :age 35}
   {:name "Park" :age 42}])
(println "People aged≥40" (->> users
     (filter #(>= (:age %) 40))
     (map :name))
)
