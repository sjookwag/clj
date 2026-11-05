(ns web.core
  "A tiny Clojure web service.

   In Clojure, a web app is just a function:
     request map  ->  response map
   Ring defines those maps; Compojure gives us routing; Jetty serves it;
   next.jdbc talks to SQLite (see web.db)."
  (:require [compojure.core :refer [routes GET POST DELETE]]
            [compojure.route :as route]
            [hiccup2.core :as h]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.util.response :as resp]
            [web.db :as db])
  (:gen-class))

;; ---------------------------------------------------------------------------
;; HTML (Hiccup turns Clojure vectors into HTML; it escapes text for us)

(defn page [title & body]
  (-> (h/html
       [:html
        [:head [:meta {:charset "utf-8"}] [:title title]]
        [:body {:style "font-family: sans-serif; max-width: 40rem; margin: 2rem auto;"}
         body]])
      str
      resp/response
      (resp/content-type "text/html; charset=utf-8")))

(defn greeting [name]
  (if (seq name)
    (str "Hello, " name "!")
    "Hello, stranger!"))

(defn home-page [ds]
  (page "Hello"
        [:h1 "Hello World"]
        [:p "Type your name and press Send — it will be saved in the guestbook:"]
        [:form {:action "/greet" :method "post"}
         [:input {:name "name" :placeholder "your name" :required true}]
         [:button "Send"]]
        [:h2 "Recent visitors (" (db/visitor-count ds) " total)"]
        [:ul
         (for [{:keys [name created_at]} (db/recent-visitors ds 10)]
           [:li name " — " [:small created_at]])]
        [:p "Or try: " [:a {:href "/hello?name=Clojure"} "/hello?name=Clojure"]
         " (not saved)"]))

;; ---------------------------------------------------------------------------
;; Handlers

(defn greet-and-save [ds name]
  (when (seq name) (db/add-visitor! ds name))
  (page "Greeting"
        [:h1 (greeting name)]
        [:p "You've been added to the guestbook. " [:a {:href "/"} "Back"]]))

(defn parse-id [s] (parse-long s))   ; nil if not a number

(defn api-add-visitor [ds {:keys [name]}]
  (if (and (string? name) (seq name))
    (resp/created "/api/visitors" (db/add-visitor! ds name))
    (resp/bad-request {:error "JSON body must contain a non-empty \"name\""})))

(defn api-get-visitor [ds id]
  (if-let [v (some->> (parse-id id) (db/get-visitor ds))]
    (resp/response v)
    (resp/not-found {:error "no such visitor"})))

(defn api-delete-visitor [ds id]
  (if (some->> (parse-id id) (db/delete-visitor! ds))
    {:status 204 :headers {} :body nil}
    (resp/not-found {:error "no such visitor"})))

;; ---------------------------------------------------------------------------
;; Routes
;;
;; `make-routes` closes over the datasource, so handlers get `ds` without
;; any global state. Tests can build an app with their own database.

(defn make-routes [ds]
  (routes
   (GET    "/"                 []     (home-page ds))
   (GET    "/hello"            [name] (page "Greeting" [:h1 (greeting name)]))
   (POST   "/greet"            [name] (greet-and-save ds name))
   (GET    "/api/greet/:name"  [name] (resp/response {:message (greeting name)}))
   (POST   "/api/echo"         {body :body}
           (resp/response {:you-sent body
                           :keys (when (map? body) (sort (map name (keys body))))}))
   (GET    "/api/visitors"     []     (resp/response (db/recent-visitors ds 100)))
   (POST   "/api/visitors"     {body :body} (api-add-visitor ds body))
   (GET    "/api/visitors/:id" [id]   (api-get-visitor ds id))
   (DELETE "/api/visitors/:id" [id]   (api-delete-visitor ds id))
   (route/not-found "Not Found")))

(defn make-app
  "Builds the full Ring handler. Middleware wraps the handler, adding behaviour."
  [ds]
  (-> (make-routes ds)
      (wrap-json-body {:keywords? true})
      wrap-json-response
      wrap-params))

;; ---------------------------------------------------------------------------
;; Entry point

(defn -main [& [port]]
  (let [port    (Integer/parseInt (or port (System/getenv "PORT") "3000"))
        db-path (or (System/getenv "DB_PATH") "data/app.db")
        ds      (db/datasource db-path)]
    (db/migrate! ds)
    (println (str "Database: " db-path))
    (println (str "Server running on http://localhost:" port))
    (run-jetty (make-app ds) {:port port :join? true})))

(comment
  ;; REPL workflow
  (def ds (db/datasource "data/app.db"))
  (db/migrate! ds)
  (def server (run-jetty (fn [req] ((make-app ds) req)) ; rebuilt per request,
                         {:port 3000 :join? false}))     ; so re-eval'd code is live
  (db/recent-visitors ds 5)
  (db/add-visitor! ds "from the REPL")
  (.stop server))
