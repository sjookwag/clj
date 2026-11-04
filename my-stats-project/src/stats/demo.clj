(ns stats.demo
  (:require [stats.descriptive :as st]
            [clojure.pprint :refer [pprint]]))

;; 가상의 금융 자산 일간 수익률(%) 데이터셋
(def daily-returns
  [-1.25, 0.45, 0.12, -0.05, 0.88, 1.45, -2.10, 0.35,
   0.45, -0.30, 0.72, 2.80, -0.15, 0.45, -0.65])

(defn -main []
  (println "=== 개별 통계량 확인 ===")
  (println "표본 수   :" (count daily-returns))
  (println "평균      :" (st/mean daily-returns))
  (println "중앙값    :" (st/median daily-returns))
  (println "최빈값    :" (st/mode daily-returns))
  (println "표준편차  :" (st/std-dev daily-returns))
  (println "IQR (Q3-Q1):" (st/iqr daily-returns))
  (println "왜도      :" (st/skewness daily-returns))
  (println "초과첨도  :" (st/kurtosis daily-returns))

  (println "\n=== 요약 통계 맵 (Summary Map) ===")
  (pprint (st/summary daily-returns)))

(-main)
