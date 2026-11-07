(def products
  [{:name "Apple" :price 1000}
   {:name "Banana" :price 500}
   {:name "Orange" :price 1500}])
;; 1.상품 이름만 추출하기
(println (map :name products))
;; 2.가격이 1000 이상인 상품만 찾기
(println (filter #(>= (:price %) 1000) products))
;; 3.모든 상품에 :sale-price를 추가해서 가격의 90%로 만들기
(println 
  (map 
    #(assoc 
       % 
       :sale-price 
       (* (:price %) 0.9)
     ) 
     products
  )
)
