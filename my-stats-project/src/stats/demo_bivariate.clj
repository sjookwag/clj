(ns stats.demo-bivariate
  (:require [stats.bivariate :as bi]
            [clojure.pprint :refer [pprint]]))

;; 가상의 10영업일 수익률(%) 데이터
(def spy-returns  [ 0.85, -0.42,  1.10, -0.95,  0.30,  1.45, -1.80,  0.55, -0.20,  0.90])
(def qqq-returns  [ 1.20, -0.65,  1.55, -1.30,  0.45,  2.10, -2.40,  0.80, -0.15,  1.35])
(def tlt-returns  [-0.30,  0.40, -0.55,  0.60, -0.10, -0.80,  0.95, -0.25,  0.15, -0.40])

(defn -main []
  (println "=== 개별 자산 간 통계량 ===")
  (println "Cov(SPY, QQQ) :" (bi/covariance spy-returns qqq-returns))
  (println "Corr(SPY, QQQ):" (bi/pearson-correlation spy-returns qqq-returns))
  (println "Cov(SPY, TLT) :" (bi/covariance spy-returns tlt-returns))
  (println "Corr(SPY, TLT):" (bi/pearson-correlation spy-returns tlt-returns))

  (println "\n=== 자산 포트폴리오 상관계수 행렬 (Correlation Matrix) ===")
  (pprint (bi/correlation-matrix {:SPY spy-returns
                                  :QQQ qqq-returns
                                  :TLT tlt-returns})))

(-main)
