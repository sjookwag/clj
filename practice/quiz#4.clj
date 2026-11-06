(def book
  {:title "Clojure101"
   :price 30000
   :stock 5})
;; 1.가격 조회
(println "book price:" (get book :price))
;; 2.재고를 1 증가
(println "stock in 1:"
  (get (update book :stock inc)
       :stock
  )
)
;; 3.:author "Jim" 추가
(println 
  (assoc book :author "Jim")
)
;; 4.:stock 삭제
(println
  (dissoc book :stock)
)
;; 5.가격을 10% 인상
(println
  (update book :price #(* % 1.1))
)
