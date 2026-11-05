# my-web-project

A minimal Clojure web service (Ring + Compojure + Jetty + Hiccup),
with a SQLite guestbook via next.jdbc.

## Run

    clojure -M:run          # http://localhost:3000
    clojure -M:run 8080     # custom port
    clojure -M:test         # run tests (each uses a throwaway temp DB)

The database lives in `data/app.db` (created automatically; override with `DB_PATH`).

## Endpoints

| Method | Path               | What it does                          |
|--------|--------------------|---------------------------------------|
| GET    | `/`                | "Hello World" page with a form        |
| GET    | `/hello?name=X`    | Greets X (query string)               |
| POST   | `/greet`           | Greets + saves the name from the form |
| GET    | `/api/greet/:name` | JSON greeting                         |
| POST   | `/api/echo`        | Echoes back the JSON you send         |
| GET    | `/api/visitors`    | List visitors (JSON)                  |
| POST   | `/api/visitors`    | Add visitor `{"name": "..."}`         |
| GET    | `/api/visitors/:id`| One visitor                           |
| DELETE | `/api/visitors/:id`| Delete a visitor                      |

    curl localhost:3000/api/greet/World
    curl -H 'Content-Type: application/json' -d '{"a":1}' localhost:3000/api/echo
    curl -H 'Content-Type: application/json' -d '{"name":"Ada"}' localhost:3000/api/visitors
    curl -X DELETE localhost:3000/api/visitors/1

Inspect the DB directly: `sqlite3 data/app.db 'select * from visitors'`
