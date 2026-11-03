(defn make-user-service [db]
  (fn [id]
    (get db id)))
(def user-service
  (make-user-service
   {1 {:name "Kim"}
    2 {:name "Lee"}}))

(println "A user with id=1:" (user-service 1))
