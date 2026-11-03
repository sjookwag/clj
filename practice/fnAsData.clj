(def operations
  [+
   -
   *
   /])

(println "((operation 0) 10 20):" 
  ((operations 0) 10 20)
)

(def operations
  [inc dec #(* % 2) #(* % %)]
)

(println "[inc dec #(* % 2) #(* % %):" 
  (map #(% 10) operations)
)

(def handlers
  {:add +
   :sub -
   :mul *
   :div /})
(println "((handlers :mul) 3 4):" 
  ((handlers :mul) 3 4)
)

(println
  "(map #(% 10) [inc dec #(* % 2) #(* % %)]):"
   (map #(% 10)
    [inc dec #(* % 2) #(* % %)]
  )
)
