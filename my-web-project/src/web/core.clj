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

(def responsive-styles
  "
  :root {
    --primary: #4f46e5;
    --primary-hover: #4338ca;
    --bg: #f8fafc;
    --surface: #ffffff;
    --text-main: #0f172a;
    --text-sub: #475569;
    --border: #e2e8f0;
    --radius: 12px;
    --shadow: 0 4px 6px -1px rgb(0 0 0 / 0.07), 0 2px 4px -2px rgb(0 0 0 / 0.07);
  }
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    background-color: var(--bg);
    color: var(--text-main);
    line-height: 1.6;
    min-height: 100vh;
    display: flex;
    flex-direction: column;
  }
  a { color: var(--primary); text-decoration: none; }
  a:hover { text-decoration: underline; }
  .container {
    width: 100%;
    max-width: 960px;
    margin: 0 auto;
    padding: 0 1.25rem;
  }
  header {
    position: sticky;
    top: 0;
    z-index: 100;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(8px);
    border-bottom: 1px solid var(--border);
  }
  .navbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 60px;
  }
  .logo {
    font-size: 1.2rem;
    font-weight: 800;
    color: var(--primary);
    text-decoration: none !important;
  }
  .nav-links {
    display: flex;
    gap: 1.5rem;
    list-style: none;
  }
  .nav-links a {
    color: var(--text-sub);
    font-weight: 500;
    text-decoration: none;
  }
  .nav-links a:hover { color: var(--primary); }
  .menu-toggle {
    display: none;
    background: none;
    border: none;
    font-size: 1.5rem;
    cursor: pointer;
    color: var(--text-main);
  }
  main { flex: 1; padding: 2.5rem 0; }
  .hero {
    text-align: center;
    padding: 2rem 1rem 2.5rem;
  }
  .hero h1 {
    font-size: clamp(1.85rem, 4.5vw, 2.75rem);
    line-height: 1.2;
    margin-bottom: 0.75rem;
  }
  .hero p {
    color: var(--text-sub);
    font-size: clamp(0.95rem, 2vw, 1.1rem);
    max-width: 560px;
    margin: 0 auto;
  }
  .grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 1.5rem;
    margin-top: 1rem;
  }
  .card {
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: var(--radius);
    padding: 1.5rem;
    box-shadow: var(--shadow);
  }
  .card h2 {
    font-size: 1.25rem;
    margin-bottom: 1rem;
  }
  .guestbook-form {
    display: flex;
    gap: 0.75rem;
    flex-wrap: wrap;
    margin-top: 1rem;
  }
  .guestbook-form input {
    flex: 1 1 200px;
    padding: 0.7rem 1rem;
    border: 1px solid var(--border);
    border-radius: 8px;
    font-size: 1rem;
    outline: none;
  }
  .guestbook-form input:focus {
    border-color: var(--primary);
    box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.15);
  }
  .guestbook-form button, .btn {
    padding: 0.7rem 1.4rem;
    background: var(--primary);
    color: #fff;
    border: none;
    border-radius: 8px;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    transition: background 0.2s;
    text-decoration: none !important;
    display: inline-block;
  }
  .guestbook-form button:hover, .btn:hover {
    background: var(--primary-hover);
  }
  .visitor-list {
    list-style: none;
    display: flex;
    flex-direction: column;
    gap: 0.6rem;
  }
  .visitor-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 0.5rem;
    padding: 0.65rem 0.9rem;
    background: var(--bg);
    border-radius: 8px;
    border: 1px solid var(--border);
  }
  .visitor-item small { color: var(--text-sub); }
  .helper-link { margin-top: 1.25rem; font-size: 0.9rem; color: var(--text-sub); }
  footer {
    border-top: 1px solid var(--border);
    background: var(--surface);
    padding: 1.25rem 0;
    text-align: center;
    color: var(--text-sub);
    font-size: 0.875rem;
  }
  @media (max-width: 640px) {
    .menu-toggle { display: block; }
    .nav-links {
      display: none;
      position: absolute;
      top: 60px;
      left: 0;
      right: 0;
      background: var(--surface);
      flex-direction: column;
      gap: 0;
      border-bottom: 1px solid var(--border);
      box-shadow: var(--shadow);
    }
    .nav-links.active { display: flex; }
    .nav-links li { border-top: 1px solid var(--border); }
    .nav-links a { display: block; padding: 0.9rem 1.25rem; }
    .guestbook-form { flex-direction: column; }
    .guestbook-form button { width: 100%; }
  }
  ")

(defn page [title & body]
  (-> (h/html
       (h/raw "<!DOCTYPE html>")
       [:html {:lang "ko"}
        [:head
         [:meta {:charset "utf-8"}]
         [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
         [:title title]
         [:style (h/raw responsive-styles)]]
        [:body
         [:header
          [:div.container.navbar
           [:a.logo {:href "/"} "Clojure Guestbook"]
           [:button.menu-toggle {:id "menuToggle" :aria-label "Toggle menu"} "☰"]
           [:ul.nav-links {:id "navLinks"}
            [:li [:a {:href "/"} "Home"]]
            [:li [:a {:href "/hello?name=Clojure"} "Hello Demo"]]
            [:li [:a {:href "/api/visitors"} "Visitors API"]]]]]
         [:main.container
          body]
         [:footer
          [:div.container "Powered by Clojure, Ring, Compojure & Hiccup"]]
         [:script
          (h/raw "const btn=document.getElementById('menuToggle'),nav=document.getElementById('navLinks');if(btn&&nav){btn.addEventListener('click',()=>{nav.classList.toggle('active');btn.textContent=nav.classList.contains('active')?'✕':'☰';});}")]]])
      str
      resp/response
      (resp/content-type "text/html; charset=utf-8")))

(defn greeting [name]
  (if (seq name)
    (str "Hello, " name "!")
    "Hello, stranger!"))

(defn home-page [ds]
  (page "Hello"
        [:section.hero
         [:h1 "Hello World"]
         [:p "Type your name and press Send — it will be saved in the guestbook:"]]
        [:div.grid
         [:section.card
          [:h2 "Sign the Guestbook"]
          [:form.guestbook-form {:action "/greet" :method "post"}
           [:input {:name "name" :placeholder "your name" :required true}]
           [:button {:type "submit"} "Send"]]
          [:p.helper-link "Or try: " [:a {:href "/hello?name=Clojure"} "/hello?name=Clojure"]
           " (not saved)"]]
         [:section.card
          [:h2 "Recent visitors (" (db/visitor-count ds) " total)"]
          [:ul.visitor-list
           (for [{:keys [name created_at]} (db/recent-visitors ds 10)]
             [:li.visitor-item
              [:strong name]
              [:small created_at]])]]]))

;; ---------------------------------------------------------------------------
;; Handlers

(defn greet-and-save [ds name]
  (when (seq name) (db/add-visitor! ds name))
  (page "Greeting"
        [:section.card {:style "max-width: 32rem; margin: 2rem auto; text-align: center;"}
         [:h1 {:style "margin-bottom: 0.75rem;"} (greeting name)]
         [:p {:style "margin-bottom: 1.5rem; color: var(--text-sub);"}
          "You've been added to the guestbook."]
         [:a.btn {:href "/"} "Back"]]))

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
