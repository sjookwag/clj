(defn gugudan [n]
  (doseq [i (range 1 10)]
    (println n "x" i "=" (* n i))))

(gugudan 3)
