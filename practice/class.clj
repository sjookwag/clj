(def person
  {:name "Jim"
   :age 30})
(defn greet [person]
  (println "Hello" (:name person)))
(greet person)
