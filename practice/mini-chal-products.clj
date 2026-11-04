(def products
  [{:name "Apple" :price 1000}
   {:name "Banana" :price 500}
   {:name "Orange" :price 1500}
   {:name "Melon" :price 3000}])
(println "products over $1000:\n"
  (->> products
     (filter #(>= (:price %) 1000))
     (map :name))
)

(println "sum of product prices:\n"
  (reduce + (map :price products))
)
