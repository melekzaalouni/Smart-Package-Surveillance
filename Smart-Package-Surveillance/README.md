# Smart Package Surveillance — Backend

ESP32 -> Spring Boot -> PostgreSQL -> Android app (roles: CUSTOMER, ADMIN)

## Run
1. `CREATE DATABASE smart_package;` (PostgreSQL) — edit user/password in `application.properties`
2. `./mvnw spring-boot:run`  (no Postgres? `./mvnw spring-boot:run -Dspring-boot.run.profiles=h2`)
3. Default admin: `admin@smart.com` / `Admin12345` (created at first start)

## Web console
Open `http://localhost:8080` after starting the app. The UI is a single file: `src/main/resources/static/index.html` (Signal & Steel design, light/dark).

## API
| Method | URL | Auth | Purpose |
|---|---|---|---|
| POST | /api/auth/register | - | create CUSTOMER |
| POST | /api/auth/login | - | returns JWT + role |
| GET | /api/users/me | JWT | my profile |
| GET | /api/packages/my | JWT | my packages |
| GET | /api/packages/{code} | JWT (owner/admin) | package detail |
| GET | /api/packages/{code}/tracking | JWT (owner/admin) | latest temp/humidity/GPS/door/shock |
| GET | /api/packages/{code}/history?limit=60 | JWT (owner/admin) | recent readings (charts) |
| GET | /api/packages/{code}/alerts | JWT (owner/admin) | alerts of the package |
| POST | /api/sensor-data | `X-Device-Key` | ESP32 reading |
| POST/GET | /api/admin/containers | ADMIN | create / list containers |
| GET | /api/admin/containers/{code}/sensor-data?limit=60 | ADMIN | history (max 500) |
| PATCH | /api/admin/containers/{code}/status | ADMIN | `{"status":"MAINTENANCE"}` |
| POST/GET | /api/admin/packages | ADMIN | create (assign customer + container) / list |
| PATCH | /api/admin/packages/{code}/status | ADMIN | CREATED, IN_TRANSIT, DELIVERED, CANCELLED |
| GET | /api/admin/alerts?unresolved=true | ADMIN | alerts |
| PATCH | /api/admin/alerts/{id}/resolve | ADMIN | resolve |
| GET | /api/admin/users | ADMIN | all users |
| PATCH | /api/admin/users/role | ADMIN | `{"email":"..","role":"ADMIN"}` promote/demote |

Send the token as `Authorization: Bearer <jwt>`.

## Quick test
```bash
TOKEN=$(curl -s -X POST localhost:8080/api/auth/login -H 'Content-Type: application/json' \
  -d '{"email":"admin@smart.com","password":"Admin12345"}' | sed 's/.*"token":"\([^"]*\)".*/\1/')
curl -X POST localhost:8080/api/admin/containers -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"containerCode":"CONT-0001","name":"Container 1"}'
curl -X POST localhost:8080/api/auth/register -H 'Content-Type: application/json' \
  -d '{"username":"client","email":"client@test.com","password":"Client1234"}'
curl -X POST localhost:8080/api/admin/packages -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"customerEmail":"client@test.com","containerCode":"CONT-0001","currentLocation":"Tunis"}'
curl -X POST localhost:8080/api/sensor-data -H 'X-Device-Key: esp32-secret-key-change-me' -H 'Content-Type: application/json' \
  -d '{"containerCode":"CONT-0001","temperature":34.5,"humidity":55,"latitude":35.76,"longitude":10.81,"opened":true,"shockDetected":false}'
```
That last call creates HIGH_TEMPERATURE and DOOR_OPENED alerts.

Firmware: `firmware/esp32_sensor.ino` (set WiFi, server IP, container code).
Alert thresholds: `alert.temperature.max`, `alert.humidity.max`. Change `jwt.secret`, `device.api-key`, admin password before deploying.
