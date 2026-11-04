(ns stats.bivariate
  (:require [stats.descriptive :as st]))

(defn covariance
  "표본 공분산 (Sample Covariance, df = N - 1):
   Cov(X, Y) = sum((X_i - mean_X) * (Y_i - mean_Y)) / (N - 1)"
  [xs ys]
  (let [n (count xs)]
    (when (and (> n 1) (= n (count ys)))
      (let [mean-x (st/mean xs)
            mean-y (st/mean ys)
            cross-diff-sum (reduce + (map (fn [x y]
                                            (* (- (double x) mean-x)
                                               (- (double y) mean-y)))
                                          xs ys))]
        (/ cross-diff-sum (dec n))))))

(defn covariance-fast
  "Java primitive loop를 사용한 빠른 단일 패스 공분산 계산"
  [^doubles xs ^doubles ys]
  (let [n (alength xs)]
    (when (and (> n 1) (= n (alength ys)))
      (let [mean-x (st/mean xs)
            mean-y (st/mean ys)]
        (loop [i 0
               acc 0.0]
          (if (< i n)
            (recur (unchecked-inc i)
                   (+ acc (* (- (aget xs i) mean-x)
                             (- (aget ys i) mean-y))))
            (/ acc (double (dec n)))))))))

(defn pearson-correlation
  "피어슨 상관계수 (Pearson Correlation Coefficient, r):
   r = Cov(X, Y) / (StdDev(X) * StdDev(Y))
   -1.0 <= r <= 1.0 범위 반환"
  [xs ys]
  (let [n (count xs)]
    (when (and (> n 1) (= n (count ys)))
      (let [cov (covariance xs ys)
            std-x (st/std-dev xs)
            std-y (st/std-dev ys)
            denom (* std-x std-y)]
        (if (pos? denom)
          (/ cov denom)
          0.0)))))

(defn correlation-matrix
  "자산/변량 리스트(컬럼 단위 벡터)를 받아 N x N 상관계수 대칭 행렬을 반환합니다."
  [series-map]
  (let [keys-seq (vec (keys series-map))]
    (reduce
     (fn [acc [k1 k2]]
       (let [r (pearson-correlation (get series-map k1) (get series-map k2))]
         (-> acc
             (assoc-in [k1 k2] r)
             (assoc-in [k2 k1] r))))
     {}
     (for [i (range (count keys-seq))
           j (range i (count keys-seq))]
       [(nth keys-seq i) (nth keys-seq j)]))))
