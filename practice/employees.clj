(def employees
  [{:name "Kim" :salary 5000}
   {:name "Lee" :salary 7000}
   {:name "Park" :salary 4000}])

;; 1.모든 이름 추출
;; 2.급여 합계 계산
;; 3.급여 5000 이상 직원만 추출을 map, filter, reduce로 작성해 보세요.

(println "1.extract names:" (map :name employees))
(println "2.sum of salary:" (reduce + (map :salary employees)))
(println "3.employees paid>5k:" (filter #(>= (:salary %) 5000) employees))
(println "4.ascending salary:" (sort-by :salary employees))
