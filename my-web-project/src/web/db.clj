(ns web.db
  "Database access with next.jdbc + SQLite.

   next.jdbc works with a *datasource* (`ds`). Every function here takes `ds`
   as its first argument instead of using a global, so tests can pass in
   their own throwaway database."
  (:require [clojure.java.io :as io]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [next.jdbc.sql :as sql]))

;; Return rows as plain maps like {:id 1 :name "Kim"}.
;; (The default would be namespaced keys like :visitors/id.)
(def ^:private opts {:builder-fn rs/as-unqualified-lower-maps})

(defn datasource
  "Creates a datasource for a SQLite file, creating its folder if needed."
  [path]
  (some-> (io/file path) .getParentFile .mkdirs)
  (jdbc/with-options (jdbc/get-datasource {:dbtype "sqlite" :dbname path}) opts))

(defn migrate!
  "Creates the tables if they don't exist yet. Safe to run on every start."
  [ds]
  (jdbc/execute! ds ["CREATE TABLE IF NOT EXISTS visitors (
                        id         INTEGER PRIMARY KEY AUTOINCREMENT,
                        name       TEXT NOT NULL,
                        created_at TEXT NOT NULL DEFAULT (datetime('now')))"]))

;; --- CRUD -------------------------------------------------------------------

(defn add-visitor!
  "Inserts a visitor and returns the new row."
  [ds name]
  (let [{:keys [id]} (jdbc/execute-one! ds ["INSERT INTO visitors (name) VALUES (?) RETURNING id" name])]
    (sql/get-by-id ds :visitors id)))

(defn recent-visitors [ds limit]
  ;; Always use ? placeholders for user input — never build SQL with `str`.
  (jdbc/execute! ds ["SELECT * FROM visitors ORDER BY id DESC LIMIT ?" limit]))

(defn get-visitor [ds id]
  (sql/get-by-id ds :visitors id))

(defn delete-visitor!
  "Returns true if a row was deleted."
  [ds id]
  (pos? (::jdbc/update-count (sql/delete! ds :visitors {:id id}))))

(defn visitor-count [ds]
  (:n (jdbc/execute-one! ds ["SELECT count(*) AS n FROM visitors"])))
