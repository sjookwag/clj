(def user
  {:name "Kim"
   :age 30
   :city "Seoul"})

(def user3
  {:name "Jim"
   :address {:city "Toronto"
             :postal "A1B2C3"}})

(println "(type :name)" (type :name))
(println {:name "Kim"})
(println (get user :name))
(println (get user :phone "없음"))

(def user2
  (assoc user :age 31))

(println user2)
(println (assoc user :email "kim@test.com"))
(println (dissoc user :city))
(println (update user :age inc))
(println (update user :age #(+ % 10)))

(println (get-in user3 [:address :city]))

(println 
  (get-in 
    (assoc-in user3 [:address :city] "Vancouver") 
    [:address :city]
  )
)

(println user3)
(println 
  (get-in 
    (update-in user3 [:address :city] clojure.string/upper-case)
    [:address :city] 
  )
)
