(def a (atom 5))
(compare-and-set! a 5 10) ;=> true  (현재값이 5일 때만 10으로)
(compare-and-set! a 5 20) ;=> false (이미 10이라 실패)
(println @a)
