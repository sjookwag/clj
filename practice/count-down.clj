(defn countdown [n]
  (when (pos? n)
    (println n)
    (recur (dec n))))

(countdown 10)
