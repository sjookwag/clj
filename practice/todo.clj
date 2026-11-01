(def todos
  ["장보기"
   "운동하기"
   "Clojure 공부"])

(doseq [todo todos]
  (println "-" todo))
