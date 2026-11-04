(defn make-logger [prefix]
  (fn [msg]
    (println prefix ":" msg)))
(def info-log
  (make-logger "INFO"))

(def error-log
  (make-logger "ERROR"))

(info-log "서버 시작")
(error-log "DB 연결 실패")
(println (info-log "서버 시작"))
