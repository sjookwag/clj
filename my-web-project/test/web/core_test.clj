(ns web.core-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [clojure.string :as str]
            [cheshire.core :as json]
            [web.core :refer [make-app]]
            [web.db :as db]))

;; Each test gets a fresh SQLite file in the temp dir, deleted afterwards.
(def ^:dynamic *app* nil)
(def ^:dynamic *ds* nil)

(use-fixtures :each
  (fn [t]
    (let [f  (java.io.File/createTempFile "web-test" ".db")
          ds (db/datasource (.getPath f))]
      (db/migrate! ds)
      (binding [*ds* ds *app* (make-app ds)]
        (try (t) (finally (.delete f)))))))

(defn req [method uri & {:as extra}]
  (*app* (merge {:request-method method :uri uri} extra)))

(defn json-req [method uri data]
  (req method uri
       :headers {"content-type" "application/json"}
       :body (java.io.ByteArrayInputStream. (.getBytes (json/generate-string data)))))

(defn body-json [res] (json/parse-string (:body res) true))

(deftest home-test
  (let [res (req :get "/")]
    (is (= 200 (:status res)))
    (is (str/includes? (:body res) "Hello World"))))

(deftest greet-test
  (testing "query parameter"
    (is (str/includes? (:body (req :get "/hello" :query-string "name=Kim")) "Hello, Kim!")))
  (testing "JSON API"
    (is (str/includes? (:body (req :get "/api/greet/Lee")) "Hello, Lee!"))))

(deftest form-post-saves-visitor
  (req :post "/greet"
       :headers {"content-type" "application/x-www-form-urlencoded"}
       :body (java.io.ByteArrayInputStream. (.getBytes "name=Park")))
  (is (= ["Park"] (map :name (db/recent-visitors *ds* 10))))
  (is (str/includes? (:body (req :get "/")) "Park")))

(deftest visitors-api
  (let [created (json-req :post "/api/visitors" {:name "Choi"})
        id      (:id (body-json created))]
    (is (= 201 (:status created)))
    (is (= "Choi" (:name (body-json (req :get (str "/api/visitors/" id))))))
    (is (= 1 (count (body-json (req :get "/api/visitors")))))
    (is (= 204 (:status (req :delete (str "/api/visitors/" id)))))
    (is (= 404 (:status (req :get (str "/api/visitors/" id)))))))

(deftest visitors-api-validation
  (is (= 400 (:status (json-req :post "/api/visitors" {:nom "typo"}))))
  (is (= 404 (:status (req :get "/api/visitors/abc")))))

(deftest sql-injection-is-harmless
  (db/add-visitor! *ds* "Robert'); DROP TABLE visitors;--")
  (is (= 1 (db/visitor-count *ds*))))

(deftest not-found-test
  (is (= 404 (:status (req :get "/nope")))))
