# Job Application Tracker API

A Spring Boot REST API for tracking job applications and the notes attached to them.
It authenticates with JSON Web Tokens, stores data in PostgreSQL, and versions the
schema with Flyway.

This repository holds the API. The
[web client](https://github.com/Anshika-320/job-application-tracker-fe) is a React
app that runs against it. You can use the API on its own with curl or any HTTP
client, and the screenshots below show the client that consumes it.

![Dashboard](https://raw.githubusercontent.com/Anshika-320/job-application-tracker/main/docs/screenshots/dashboard.png)

## What it does

The API signs a user in against a BCrypt password hash and returns a token. A servlet
filter reads that token on every later request and rejects anything invalid, expired
or belonging to a deleted account with a 401.

Applications support the usual create, read, update and delete operations, with
`created_at` and `updated_at` maintained by JPA lifecycle callbacks. Separate routes
filter by status, filter by location, search by company name, and return a paged,
sorted listing. One route returns the note count per application in a single query.

Notes belong to an application and disappear with it, through both the JPA cascade
and an `ON DELETE CASCADE` on the foreign key.

Bean validation runs before any request reaches the service layer, and validation
failures come back as a map of field to message. An origin allowlist drives CORS, so
a client deployed somewhere else can call the API without loosening authentication.

![Notes](https://raw.githubusercontent.com/Anshika-320/job-application-tracker/main/docs/screenshots/notes.png)

## Built with

| Concern | Choice |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot 4.1 |
| Security | Spring Security and JJWT |
| Persistence | Spring Data JPA over Hibernate |
| Database | PostgreSQL |
| Migrations | Flyway |
| Build | Maven, wrapper committed |
| Tests | JUnit 5, MockMvc, Mockito |

## Requirements

Install these before you start.

| Software | Version | Check with | Notes |
| --- | --- | --- | --- |
| JDK | 17 or newer | `java -version` | Any distribution works: Temurin, Corretto, Zulu, Oracle |
| PostgreSQL | 13 or newer | `psql --version` | The server has to be running and reachable |
| Git | any | `git --version` | To clone the repository |
| curl | any | `curl --version` | Only `scripts/seed.sh` uses it. Already present on macOS and most Linux distributions |

You do not need Maven installed. The wrapper is committed, so use `./mvnw` on macOS
and Linux, or `mvnw.cmd` on Windows. The first build downloads Maven and the
dependencies, which needs network access and takes a few minutes. Later builds run
offline.

On macOS, install PostgreSQL with [Postgres.app](https://postgresapp.com) or
`brew install postgresql@16`. On Debian or Ubuntu, `sudo apt install postgresql`.

To use the web client as well, you need Node.js 20.19 or newer. Its repository covers
that setup.

## Setup

### 1. Clone and create the database

```bash
git clone https://github.com/Anshika-320/job-application-tracker.git
cd job-application-tracker
createdb job_tracker
```

If `createdb` is not on your path:

```bash
psql -U postgres -c "CREATE DATABASE job_tracker;"
```

### 2. Write the configuration file

Git ignores `src/main/resources/application.properties`, so local credentials stay
off the repository. Copy the template:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Fill in these settings.

| Setting | Purpose |
| --- | --- |
| `spring.datasource.url` | JDBC URL, for example `jdbc:postgresql://localhost:5432/job_tracker` |
| `spring.datasource.username` | Database user |
| `spring.datasource.password` | Database password |
| `jwt.secret` | Base64 HMAC key, 256 bits or longer |
| `jwt.expiration` | Token lifetime in milliseconds, `3600000` for one hour |
| `FRONTEND_ORIGIN` | Comma separated browser origins allowed to call the API. Defaults to `http://localhost:5173` |

Generate a signing key:

```bash
openssl rand -base64 32
```

Any setting also reads from an environment variable, so a deployed instance can use
`SPRING_DATASOURCE_URL`, `JWT_SECRET` and `FRONTEND_ORIGIN` instead of a file.

### 3. Create an account and start the API

There is no registration endpoint. The `dev` profile creates one account at startup
from environment variables:

```bash
SPRING_PROFILES_ACTIVE=dev \
TEST_USER_EMAIL=demo@jobtracker.local \
TEST_USER_PASSWORD='choose-a-local-password' \
./mvnw spring-boot:run
```

The log line `DEV LOGIN CHECK: SUCCESS` confirms the account exists and can
authenticate. Running the command again updates that account's password instead of
creating a second one.

Without the profile the API starts and creates nothing:

```bash
./mvnw spring-boot:run
```

Either way it listens on http://localhost:8080 and Flyway applies the migrations in
`src/main/resources/db/migration` on first run.

### 4. Load demo data

`scripts/seed.sh` creates seven applications covering all six statuses, with notes on
several of them. With the API running from step 3:

```bash
TEST_USER_EMAIL=demo@jobtracker.local \
TEST_USER_PASSWORD='choose-a-local-password' \
./scripts/seed.sh
```

```
Seeding http://localhost:8080
  signed in as demo@jobtracker.local
  creating applications
  done, 7 applications in the database
```

| Option | Behaviour |
| --- | --- |
| none | Seeds only when the database has no applications, so it never overwrites your data |
| `--reset` | Deletes every application first, then seeds |
| `--help` | Usage |

The script signs in through the real API, so it needs the account from step 3. If
sign in fails it prints the command to start the API correctly. Set `API_BASE_URL` to
point it somewhere other than `http://localhost:8080`.

## Deploying with Docker and Render

The `Dockerfile` builds the jar in one stage and runs it on a JRE in a second. It
reads the port from `PORT` (Render sets this) and takes all configuration from
environment variables, since `application.properties` is not part of the image.

Build and run locally:

```bash
docker build -t job-tracker .
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/job_tracker \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=change-me \
  -e JWT_SECRET="$(openssl rand -base64 32)" \
  -e JWT_EXPIRATION=3600000 \
  job-tracker
```

On Render:

1. Create a PostgreSQL database.
2. Create a **Web Service** from this repository. Render detects the `Dockerfile`.
3. Set these environment variables on the service:

| Variable | Value |
| --- | --- |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://<host>:5432/<database>`, built from the database's internal host and name. Render's own connection string starts with `postgresql://`, which JDBC rejects |
| `SPRING_DATASOURCE_USERNAME` | Database user |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `JWT_SECRET` | Output of `openssl rand -base64 32` |
| `JWT_EXPIRATION` | `3600000` |
| `FRONTEND_ORIGIN` | The deployed client's origin, for example `https://your-client.onrender.com` |

There is no registration endpoint, so to create the first account set
`SPRING_PROFILES_ACTIVE=dev`, `TEST_USER_EMAIL` and `TEST_USER_PASSWORD` for one
deploy, then remove them. Flyway applies the migrations on startup.

## API reference

Every route except `/api/auth/**` needs an `Authorization: Bearer <token>` header.

### Authentication

| Method | Path | Body | Returns |
| --- | --- | --- | --- |
| POST | `/api/auth/login` | `{ "email", "password" }` | `{ "token" }` |

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"demo@jobtracker.local","password":"choose-a-local-password"}'
```

### Applications

| Method | Path | Notes |
| --- | --- | --- |
| GET | `/api/applications` | Every application |
| POST | `/api/applications` | Create, returns 201 |
| GET | `/api/applications/{id}` | One application |
| PUT | `/api/applications/{id}` | Update |
| DELETE | `/api/applications/{id}` | Delete, returns 204 |
| GET | `/api/applications/status?status=` | Filter by status |
| GET | `/api/applications/search?companyName=` | Company name contains, ignoring case |
| GET | `/api/applications/location?location=` | Exact location, ignoring case |
| GET | `/api/applications/page?page=&size=&sort=` | Paged and sorted |
| GET | `/api/applications/note-counts` | Note count per application |

### Notes

| Method | Path | Notes |
| --- | --- | --- |
| GET | `/api/applications/{id}/notes` | Notes for one application |
| POST | `/api/applications/{id}/notes` | Add a note, returns 201 |
| DELETE | `/api/applications/{id}/notes/{noteId}` | Delete a note, returns 204 |

### Payloads

Creating or updating an application:

```json
{
  "companyName": "Atlassian",
  "role": "Senior Backend Engineer",
  "status": "INTERVIEWED",
  "location": "Bengaluru"
}
```

The response adds the id and the timestamps:

```json
{
  "id": 1,
  "companyName": "Atlassian",
  "role": "Senior Backend Engineer",
  "status": "INTERVIEWED",
  "location": "Bengaluru",
  "createdAt": "2026-09-18T12:39:52.194332",
  "updatedAt": "2026-09-18T12:39:52.194332"
}
```

`companyName`, `role` and `status` are required. `location` is optional.

Status accepts `APPLIED`, `INTERVIEW_SCHEDULED`, `INTERVIEWED`, `OFFERED`,
`REJECTED` and `WITHDRAWN`.

## Data model

```
app_users                job_applications                application_notes
--------------           --------------------            ----------------------
id            PK         id                 PK           id                  PK
email         unique     company_name       <=150        content             <=500
password_hash            role               <=150        job_application_id  FK
created_at               status             <=30
updated_at               location           <=150
                         created_at
                         updated_at

                         one job_applications row has many application_notes
                         rows, and deleting it deletes them
```

`app_users` only backs authentication. It has no foreign key to the other tables,
which means every signed in user sees the same applications. That suits a single user
setup. Separating users would need a `user_id` column on `job_applications` and
ownership checks in the service layer.

## Errors

Validation failures return 400 with a map of field to message, which the client
renders next to the matching input:

```json
{
  "companyName": "Company name is required",
  "role": "Role is required"
}
```

Missing records and bad values return 400 or 404 with a description:

```json
{
  "timestamp": "2026-09-18T12:39:52.366464",
  "status": 404,
  "error": "Not Found",
  "message": "Job application not found with id: 999999"
}
```

Validation limits match the column widths, so oversized input returns 400 rather than
failing when Hibernate writes the row.

## Security

Passwords are hashed with BCrypt and no endpoint returns them. Sessions are stateless
under `SessionCreationPolicy.STATELESS`, so the server keeps nothing between requests.
Every route outside `/api/auth/**` needs authentication. A token that is malformed,
expired, or signed for an account that no longer exists clears the security context
and returns 401.

CORS reads an explicit origin list from `FRONTEND_ORIGIN` and allows only the methods
and headers the client sends. No wildcard is used. Git ignores
`application.properties` and any `.env` file.

## Tests

```bash
./mvnw test
```

11 tests in three classes:

| Class | Covers |
| --- | --- |
| `JobApplicationControllerTest` | Required fields, length limits, unknown status values, a successful create |
| `ApplicationNoteControllerTest` | Blank, missing and oversized note content, a successful create |
| `SecurityConfigCorsTest` | Preflight from an allowed origin, rejection of an unknown origin, and that CORS does not skip authentication |

Build a runnable jar:

```bash
./mvnw package
java -jar target/job-tracker-0.0.1-SNAPSHOT.jar
```

## Project layout

```
src/main/java/com/anu/job_tracker/
  config/
    SecurityConfig.java        filter chain, CORS, password encoder
    DevTestUserConfig.java     dev profile account creation
  controller/                  REST endpoints
  dto/                         request and response payloads
  entity/                      JPA entities
  exception/                   ResourceNotFoundException and the global handler
  repository/                  Spring Data repositories
  security/                    JWT service, auth filter, user details
  service/                     business logic
src/main/resources/
  db/migration/                Flyway migrations V1 to V5
scripts/
  seed.sh                      demo data
```
