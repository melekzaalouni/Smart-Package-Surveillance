// ESP32 firmware: DHT22 (temp/humidity), reed switch (door), SW-420 (shock), NEO-6M GPS.
// Libraries: DHT sensor library, TinyGPSPlus, ArduinoJson (Arduino IDE -> Library Manager)
#include <WiFi.h>
#include <HTTPClient.h>
#include <DHT.h>
#include <TinyGPSPlus.h>
#include <ArduinoJson.h>

const char* WIFI_SSID = "YOUR_WIFI";
const char* WIFI_PASS = "YOUR_PASSWORD";
const char* SERVER_URL = "http://192.168.1.10:8080/api/sensor-data"; // PC running Spring Boot
const char* DEVICE_KEY = "esp32-secret-key-change-me";               // = device.api-key
const char* CONTAINER_CODE = "CONT-0001";                            // must exist in DB

#define DHT_PIN 4
#define DOOR_PIN 15    // reed switch to GND, INPUT_PULLUP: HIGH = door open
#define SHOCK_PIN 27   // SW-420 digital out: HIGH = vibration
#define GPS_RX 16
#define GPS_TX 17

DHT dht(DHT_PIN, DHT22);
TinyGPSPlus gps;
HardwareSerial gpsSerial(2);

volatile bool shockFlag = false;
void IRAM_ATTR onShock() { shockFlag = true; }

unsigned long lastSend = 0;
const unsigned long SEND_INTERVAL = 10000; // ms

void setup() {
  Serial.begin(115200);
  dht.begin();
  pinMode(DOOR_PIN, INPUT_PULLUP);
  pinMode(SHOCK_PIN, INPUT);
  attachInterrupt(digitalPinToInterrupt(SHOCK_PIN), onShock, RISING);
  gpsSerial.begin(9600, SERIAL_8N1, GPS_RX, GPS_TX);
  WiFi.begin(WIFI_SSID, WIFI_PASS);
  while (WiFi.status() != WL_CONNECTED) { delay(500); Serial.print("."); }
  Serial.println("\nWiFi connected");
}

void loop() {
  while (gpsSerial.available()) gps.encode(gpsSerial.read());

  if (millis() - lastSend < SEND_INTERVAL) return;
  lastSend = millis();

  float t = dht.readTemperature();
  float h = dht.readHumidity();
  if (isnan(t) || isnan(h)) { Serial.println("DHT read failed"); return; }

  // Fallback coordinates (Monastir) until the GPS gets a fix
  double lat = gps.location.isValid() ? gps.location.lat() : 35.7643;
  double lng = gps.location.isValid() ? gps.location.lng() : 10.8113;

  StaticJsonDocument<256> doc;
  doc["containerCode"] = CONTAINER_CODE;
  doc["temperature"] = t;
  doc["humidity"] = h;
  doc["latitude"] = lat;
  doc["longitude"] = lng;
  doc["opened"] = digitalRead(DOOR_PIN) == HIGH;
  doc["shockDetected"] = shockFlag;
  shockFlag = false;

  String body;
  serializeJson(doc, body);

  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(SERVER_URL);
    http.addHeader("Content-Type", "application/json");
    http.addHeader("X-Device-Key", DEVICE_KEY);
    int code = http.POST(body);
    Serial.printf("POST %d  %s\n", code, body.c_str());
    http.end();
  }
}
