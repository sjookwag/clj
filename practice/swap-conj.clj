(def todos (atom []))

(swap! todos conj "공부하기")
(swap! todos conj "운동하기")
(println @todos) ;=> ["공부하기" "운동하기"]
