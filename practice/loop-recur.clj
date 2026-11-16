(let  [i 0] (println i))  ; 한 번 실행하고 끝
(loop [i 0] (println i))  ; recur가 없으면 let과 똑같음

(loop [i 0]
  (when (< i 3)
    (println i)
    (recur (inc i))))
