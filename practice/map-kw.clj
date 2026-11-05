(def user
  {:name "Kim"
   :age 30
   :city "Seoul"})
(println "(type :name)" (type :name))
(println {:name "Kim"})
(println (get user :name))
(println (get user :phone "없음"))

(def user2
  (assoc user :age 31))

(println user2)
(println (assoc user :email "kim@test.com"))
(println (dissoc user :city))
