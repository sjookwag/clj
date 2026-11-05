(def counter (atom 0))

(time
  (doall
    (pmap
      (fn [_] (swap! counter inc))
      (range 100000)
    )
  )
)
@counter
