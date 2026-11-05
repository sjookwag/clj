(def account-a (ref 1000))
(def account-b (ref 1000))

(defn transfer [from to amount]
  (dosync
    (alter from - amount)
    (alter to + amount)))

;; 100개의 스레드가 동시에 이체
(doall
  (pmap
    (fn [_]
      (transfer account-a account-b 1)
      (transfer account-b account-a 1))
    (range 100000)))

(println @account-a)
(println @account-b)
