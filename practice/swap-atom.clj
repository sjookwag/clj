(def counter (atom 0))

(swap! counter inc)  ; 함수를 적용해서 바꿈 → 1
(swap! counter + 10) ; (+ 현재값 10) → 11
(reset! counter 0)   ; 값을 직접 덮어씀 → 0
(println @counter)             ;=> 0  (읽기는 @ 또는 deref)
