(ns stats.descriptive)

;; 1. 중심 경향성 (Central Tendency)

(defn mean
  "산술평균: 시퀀스의 합을 원소 개수로 나눕니다."
  [coll]
  (when (seq coll)
    (/ (double (reduce + coll)) (count coll))))

(defn median
  "중앙값: 정렬된 시퀀스의 정중앙 값 (짝수 개일 경우 중앙 두 값의 평균)."
  [coll]
  (when (seq coll)
    (let [sorted (vec (sort coll))
          n (count sorted)
          mid (quot n 2)]
      (if (odd? n)
        (double (nth sorted mid))
        (/ (+ (nth sorted (dec mid)) (nth sorted mid)) 2.0)))))

(defn mode
  "최빈값: 빈도가 가장 높은 값들의 집합을 반환합니다."
  [coll]
  (when (seq coll)
    (let [freqs (frequencies coll)
          max-freq (apply max (vals freqs))]
      (->> freqs
           (filter #(= (val %) max-freq))
           (map key)
           set))))

;; 2. 산포도 (Dispersion)

(defn variance
  "표본분산 (Sample Variance): N - 1 자유도 적용"
  [coll]
  (when (> (count coll) 1)
    (let [m (mean coll)
          sq-diffs (map #(Math/pow (- (double %) m) 2) coll)]
      (/ (reduce + sq-diffs) (dec (count coll))))))

(defn std-dev
  "표본표준편차 (Sample Standard Deviation)"
  [coll]
  (some-> (variance coll) Math/sqrt))

(defn quantile
  "분위수 (p in [0.0, 1.0]): 선형 보간(Type 7 방식) 적용"
  [p coll]
  (when (seq coll)
    (let [sorted (vec (sort coll))
          n (count sorted)]
      (if (= n 1)
        (double (first sorted))
        (let [idx (* p (dec n))
              lo (int (Math/floor idx))
              hi (int (Math/ceil idx))
              fraction (- idx lo)]
          (if (= lo hi)
            (double (nth sorted lo))
            (+ (nth sorted lo)
               (* fraction (- (nth sorted hi) (nth sorted lo))))))))))

(defn iqr
  "사분위범위 (Interquartile Range: Q3 - Q1)"
  [coll]
  (when (seq coll)
    (- (quantile 0.75 coll) (quantile 0.25 coll))))

;; 3. 왜도 및 첨도 (Shape)

(defn skewness
  "표본 왜도 (Sample Skewness): 0보다 크면 오른쪽 꼬리가 김"
  [coll]
  (let [n (count coll)]
    (when (> n 2)
      (let [m (mean coll)
            s (std-dev coll)
            m3 (->> coll
                    (map #(Math/pow (/ (- (double %) m) s) 3))
                    (reduce +))]
        (* (/ (double n) (* (dec n) (- n 2))) m3)))))

(defn kurtosis
  "초과 첨도 (Excess Kurtosis): 정규분포 기준 0"
  [coll]
  (let [n (count coll)]
    (when (> n 3)
      (let [m (mean coll)
            s (std-dev coll)
            m4 (->> coll
                    (map #(Math/pow (/ (- (double %) m) s) 4))
                    (reduce +))
            c1 (/ (* (double n) (inc n)) (* (dec n) (- n 2) (- n 3)))
            c2 (/ (* 3.0 (Math/pow (dec n) 2)) (* (- n 2) (- n 3)))]
        (- (* c1 m4) c2)))))

;; 4. 요약 통계량 (Summary)

(defn summary
  "전체 기술통계량 맵 생성"
  [coll]
  (when (seq coll)
    {:count    (count coll)
     :min      (double (apply min coll))
     :q1       (quantile 0.25 coll)
     :median   (median coll)
     :mean     (mean coll)
     :q3       (quantile 0.75 coll)
     :max      (double (apply max coll))
     :iqr      (iqr coll)
     :variance (variance coll)
     :std-dev  (std-dev coll)
     :skewness (skewness coll)
     :kurtosis (kurtosis coll)}))
