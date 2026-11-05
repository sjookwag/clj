(def products
  [{:name "Apple" :price 1000}
   {:name "Banana" :price 1500}
   {:name "Orange" :price 2000}])

(println (map :name products))
(println (filter #(>= (:price %) 2000) products))
(println (reduce + (map :price products)))
