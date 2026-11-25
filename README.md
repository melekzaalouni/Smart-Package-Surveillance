# Sentinel — Smart Package Surveillance

> An IoT parcel-security platform: smart containers report temperature, humidity, seal state, impact and GPS position in real time, and a web console turns that data into live monitoring, alerts and parcel tracking.

**Stack:** ESP32 sensors → Spring Boot 4 (Java 17) REST API → PostgreSQL (or H2) → single-file web console (HTML/CSS/JS)

---

## Description

Sentinel protects parcels in transit. Each container carries an ESP32 board that measures temperature and humidity (DHT22), detects when the door or seal is opened (reed switch), detects impacts (SW-420 vibration sensor) and reports its position (NEO-6M GPS). Readings are sent to the backend every 10 seconds.

The backend stores the history, raises **alerts** when something goes wrong (high temperature, high humidity, door opened, shock detected) and exposes a secured REST API with two roles:

- **Admin** — monitors every container, manages containers, parcels and users, resolves alerts, and can promote other users to admin.
- **Customer** — sees only their own parcels: live conditions, charts, history, alerts and delivery progress.

The web console is served by the same Spring Boot application, so there is nothing else to install.

## Features

**Monitoring**
- Live dashboard refreshed every 5 seconds: sensors connected, open alerts, parcels in transit, broken seals
- Per-container panel: temperature, humidity, seal and impact, sensor link status (Connected / Delayed / Offline)
- Temperature and humidity charts with limit lines, plus a history table
- Parcel tracking: status progress (created → in transit → delivered), location, estimated delivery, GPS position
- Security alerts with one-click resolve, a badge on the Alerts menu and a toast for each new alert

**Interface**
- A 3D parcel "digital twin" per container: the lid opens with torn red tape when the seal is broken, the crate shakes on impact, a gauge tracks the temperature
- Two complete themes — *UV inspection booth* (dark) and *customs paper* (light) — with a circular reveal when switching; the choice is saved and the OS preference is used on first visit
- Blacklight cursor: move the mouse to reveal hidden security patterns
- Three languages: **English, Français, العربية** (full right-to-left layout for Arabic)
- Animations respect the "reduce motion" system setting

**Administration**
- Create containers and parcels, change their status, search parcels
- Promote or demote users from the Users page (an admin cannot remove their own admin role)

## Architecture

```
ESP32 (DHT22 · reed · SW-420 · GPS)
        │  HTTP POST /api/sensor-data  (header X-Device-Key)
        ▼
Spring Boot REST API ──► PostgreSQL / H2
        │  JWT (Bearer token), roles ADMIN / CUSTOMER
        ▼
Web console  (src/main/resources/static/index.html)
```

## Requirements

- Java **17** or newer (Maven is included through `mvnw`)
- PostgreSQL — optional; the `h2` profile runs without any database

## Quick start

**Without a database (H2, resets on every restart):**

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

**Windows PowerShell** (the quotes are required):

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=h2"
```

**With PostgreSQL:**

```sql
CREATE DATABASE smart_package;
```
```bash
export DB_USER=postgres DB_PASSWORD=yourpassword   # PowerShell: $env:DB_USER="postgres"
./mvnw spring-boot:run
```

Then open **http://localhost:8080** and sign in with the default admin created on first start:

| Email | Password |
|---|---|
| `admin@smart.com` | `Admin12345` |

The H2 profile also enables the database console at `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:smartdb`, user `sa`, empty password).

## First steps

1. Open **Containers → New container** and create one, e.g. `CONT-0001`.
2. Register a customer on the login page (or use an existing user).
3. Open **Parcels → New parcel**, enter the customer's email and the container code.
4. Start the ESP32 (below) or send a test reading:

```bash
curl -X POST localhost:8080/api/sensor-data \
  -H "X-Device-Key: esp32-secret-key-change-me" -H "Content-Type: application/json" \
  -d '{"containerCode":"CONT-0001","temperature":34.5,"humidity":55,"latitude":35.76,"longitude":10.81,"opened":true,"shockDetected":false}'
```

That reading creates `HIGH_TEMPERATURE` and `DOOR_OPENED` alerts and the console updates within seconds.

## Hardware (ESP32)

Firmware: `firmware/esp32_sensor.ino`. Libraries: *DHT sensor library*, *TinyGPSPlus*, *ArduinoJson*.

| Sensor | Pin | Notes |
|---|---|---|
| DHT22 (temperature, humidity) | GPIO 4 | |
| Reed switch (door / seal) | GPIO 15 | to GND, `INPUT_PULLUP`; HIGH = open |
| SW-420 (shock) | GPIO 27 | interrupt; HIGH = vibration |
| NEO-6M GPS | RX 16 / TX 17 | falls back to fixed coordinates until a GPS fix |

Edit the Wi-Fi name and password, the server address (`SERVER_URL`), `DEVICE_KEY` (must equal `device.api-key`) and `CONTAINER_CODE` (must already exist) before flashing.

## Configuration

`src/main/resources/application.properties` (every value can be overridden by an environment variable):

| Property | Variable | Default |
|---|---|---|
| Database URL / user / password | `DB_URL`, `DB_USER`, `DB_PASSWORD` | local PostgreSQL, `postgres`/`postgres` |
| JWT secret | `JWT_SECRET` | development key — **change it** |
| Device key (ESP32) | `DEVICE_API_KEY` | `esp32-secret-key-change-me` — **change it** |
| Admin email / password | `ADMIN_EMAIL`, `ADMIN_PASSWORD` | `admin@smart.com` / `Admin12345` — **change it** |
| `alert.temperature.max` | | `30` (°C) |
| `alert.humidity.max` | | `80` (%) |

The console uses the same two limits (`TMAX` and `HMAX` at the top of its script in `index.html`); update both places if you change them.

## API

Send the token as `Authorization: Bearer <jwt>`.

| Method | URL | Access | Purpose |
|---|---|---|---|
| POST | `/api/auth/register` | public | create a CUSTOMER |
| POST | `/api/auth/login` | public | returns JWT and role |
| GET | `/api/users/me` | JWT | my profile |
| GET | `/api/packages/my` | JWT | my parcels |
| GET | `/api/packages/{code}` | owner / admin | parcel detail |
| GET | `/api/packages/{code}/tracking` | owner / admin | latest temperature, humidity, GPS, seal, shock |
| GET | `/api/packages/{code}/history?limit=60` | owner / admin | recent readings (charts) |
| GET | `/api/packages/{code}/alerts` | owner / admin | alerts of the parcel |
| POST | `/api/sensor-data` | `X-Device-Key` | ESP32 reading |
| GET / POST | `/api/admin/containers` | ADMIN | list / create containers |
| GET | `/api/admin/containers/{code}/sensor-data?limit=60` | ADMIN | history (max 500) |
| PATCH | `/api/admin/containers/{code}/status` | ADMIN | `{"status":"MAINTENANCE"}` |
| GET / POST | `/api/admin/packages` | ADMIN | list / create (assigns customer + container) |
| PATCH | `/api/admin/packages/{code}/status` | ADMIN | CREATED, IN_TRANSIT, DELIVERED, CANCELLED |
| GET | `/api/admin/alerts?unresolved=true` | ADMIN | alerts |
| PATCH | `/api/admin/alerts/{id}/resolve` | ADMIN | resolve an alert |
| GET | `/api/admin/users` | ADMIN | all users |
| PATCH | `/api/admin/users/role` | ADMIN | `{"email":"..","role":"ADMIN"}` promote / demote |

Container statuses: `AVAILABLE`, `IN_TRANSIT`, `ARRIVED`, `MAINTENANCE`. Alert types: `HIGH_TEMPERATURE`, `HIGH_HUMIDITY`, `DOOR_OPENED`, `SHOCK_DETECTED`.

## Project structure

```
Smart-Package-Surveillance/
├── firmware/esp32_sensor.ino              ESP32 sketch
├── src/main/java/com/SmartPackageSurveillance/
│   ├── config/        security rules, default admin
│   ├── controller/    Auth, Admin, Package, SensorData
│   ├── service/       business logic, alerts, JWT
│   ├── entity/        User, Container, Package, SensorData, Alert
│   ├── repository/    Spring Data JPA
│   ├── dto/           request / response objects
│   ├── security/      JWT filter
│   └── exception/     error handling
└── src/main/resources/
    ├── application.properties
    ├── application-h2.properties          H2 profile + console
    └── static/index.html                  the web console (HTML, CSS, JS — no build step)
```

## Security notes

- Passwords are stored as BCrypt hashes. If you insert a user with SQL, insert a BCrypt hash and the `created_at` value too.
- Role changes take effect immediately, because the role is read from the database on every request.
- Change the JWT secret, the device key and the admin password before any real deployment, and serve the API over HTTPS.
- The H2 console is only enabled by the `h2` profile; never enable it in production.

## Troubleshooting

| Problem | Fix |
|---|---|
| `Unknown lifecycle phase ".run.profiles=h2"` | PowerShell splits the argument — put `"-Dspring-boot.run.profiles=h2"` in quotes |
| `/h2-console` does not open | Run with the `h2` profile; the project needs the `spring-boot-h2console` dependency (included in `pom.xml`) |
| The page looks old after an update | Hard refresh with Ctrl+Shift+R |
| Port 8080 already in use | Stop the other process or add `--server.port=8081` |
| `java: command not found` | Install JDK 17 or newer and check `java -version` |
| Dashboard is empty | Create a container and a parcel, then send a reading (see *First steps*) |

## Roadmap

- Android app for customers (the API is ready for it)
- Live map of container positions
- MQTT or HTTPS with certificates for the device link
- Push or email notifications for critical alerts

## License

Add the license of your choice (for a student project, MIT is a common default).
