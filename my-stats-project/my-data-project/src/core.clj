
(ns core)

(defn process-numbers [nums]
  ;; ->> (스레드 라스트 매크로): 데이터를 화살표를 따라 위에서 아래로 흘려보냄
  (->> nums
       (filter even?)          ; 1. 짝수만 걸러내기 (filter)
       (map #( * % % ))        ; 2. 각각 제곱하기 (익명 함수)
       (reduce +)))            ; 3. 전부 다 더하기 (reduce)

(defn -main [& args]
  (let [my-numbers [1 2 3 4 5 6 7 8 9 10]]
    (println "=== 원본 데이터 ===")
    (println my-numbers)

    (println "\n=== 짝수들의 제곱의 합 ===")
    ;; 짝수: 2, 4, 6, 8, 10
    ;; 제곱: 4, 16, 36, 64, 100
    ;; 합계: 4 + 16 + 36 + 64 + 100 = 220
    (println (process-numbers my-numbers))
  )
)

(comment

;; 1. 재귀 방식: N번째 피보나치 수 구하기 (loop / recur)
(defn fib-at [n]
  (loop [count n
         a 0
         b 1]
    (if (zero? count)
      a
      (recur (dec count) b (+ a b)))))

;; 2. 함수형 방식: 무한 피보나치 수열 생성기 (iterate 활용)
;; [a b] 쌍을 받아 다음 [b a+b]로 계속 변환하는 무한 흐름
(def fib-seq
  (map first 
    (iterate 
      (fn [[a b]] [b (+ a b)]) 
      [0 1]
    )
  )
)

(defn -main [& args]
  (println "=== 1. 재귀로 10번째 피보나치 수 구하기 ===")
  (println (str "fib(10) = " (fib-at 10)))

  (println "\n=== 2. 피보나치 수열 앞의 10개 뽑기 ===")
  (println (take 10 fib-seq))

  (println "\n=== 3. 100 이하의 피보나치 수만 필터링 ===")
  (println (take-while #(<= % 100) fib-seq)))

;; End of comment
)
