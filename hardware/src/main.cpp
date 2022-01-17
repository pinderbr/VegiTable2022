/*
  File: VegiTable
  Authors: Jason Beattie, Bryce Pinder
  Date: March 25, 2021
  Description:
*/
#include <Arduino.h>
#include <DHT.h>
#include <DHT_U.h>
#include <WiFi.h>
#include <HTTPClient.h>
#include <string.h>
#include <EEPROM.h>
#include <DFRobot_ESP_EC.h>
#include <OneWire.h>
#include <DallasTemperature.h>
#include <ECSalinity.h>
#include <DFRobot_ESP_PH.h>
#include <WiFiManager.h>

#define DHTTYPE DHT22 // defining what type of temp/humidity sensor we are using
#define DHTPIN 14 // Temp/Humidity Pin
#define TDSPIN 35 // TDS/PPM Pin
#define LIGHTPIN 4 // Light level pin
#define WATERPIN 32 // Water level pin
#define PHPIN 25 // PH level pin
#define WATERTEMP 18 // water temperature pin
#define ESPADC 4096.0
#define ESPVOLTAGE 3300


// initialize libraries
WiFiClient client;
sensors_event_t event;
DFRobot_ESP_EC ec;
DHT_Unified dht(DHTPIN, DHTTYPE);
OneWire oneWire(WATERTEMP);
DallasTemperature sensors(&oneWire);
DFRobot_ESP_PH pH;
WiFiManager wm;


int light = 0;
float voltage, phValue, ecValue, tdsValue, waterTemp = 25;
int waterLvl = 0;
String deviceID ="1";
String tempValue ="";
String humValue ="";
String lightValue ="";
String phValueString ="";
String ppmValue ="";
String waterLevelValue ="";

const unsigned long SECOND = 1000;
const unsigned long HOUR = 3600*SECOND;

//const char *ssid = "TheTardis"; // wifi network name
//const char *password = "Doctorwho?"; // wifi password
/*void connectToNetwork()
{
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(1000);
    Serial.println("Establishing connection to WiFi..");
  }
  Serial.println("Connected to network");
} */

void readTemperature()
{
  dht.temperature().getEvent(&event);
  if (isnan(event.temperature))
  {
    Serial.println("Error reading temperature sensor");
  }
  else
  {
    tempValue=event.temperature;
  }
}

void readHumidity(){
  dht.humidity().getEvent(&event);
  if (isnan(event.relative_humidity))
  {
    Serial.println("Error reading humidity sensor.");
  }
  else
  {
    humValue=event.relative_humidity;
  }
}

void readLight(){
  // read light value
  light = analogRead(LIGHTPIN);
  lightValue = String(light);
  Serial.print("Light level: ");
  Serial.println(lightValue);
}

float readWaterTemp(){
  // read water temp
  sensors.requestTemperatures();
  return sensors.getTempCByIndex(0);
}

void readWaterLevel(){
    // read water level
  waterLvl = analogRead(WATERPIN);
  waterLevelValue = String(waterLvl);
  Serial.print("Water Level: ");
  Serial.println(waterLevelValue);
}

void readPH(){
  // get ph
  voltage = analogRead(PHPIN) / ESPADC * ESPVOLTAGE;
  waterTemp = readWaterTemp();
  phValue = (pH.readPH(voltage, waterTemp)) * -1;
  pH.calibration(voltage, waterTemp);
  phValueString = String(phValue);
  Serial.println("PH: " + phValueString);
}

void readPPM(){
  // get ec and convert to ppm
  voltage = analogRead(TDSPIN) / 10;
  waterTemp = readWaterTemp();
  tdsValue = ec.readEC(voltage, waterTemp);
  ec.calibration(voltage, waterTemp);
  ppmValue = String(tdsValue*500);
  Serial.println("PPM: " + ppmValue);
}

void sendPHP(){

  String httpRequestData = "humValue=" + 
    humValue + "&tempValue=" + tempValue + "&lightValue=" + lightValue + "&phValue=" + phValueString + "&ppmValue=" + ppmValue + "&waterValue=" + waterLevelValue;

  if(client.connect("35.182.159.177", 80)){
      client.println("POST /deviceToDb/pushToDb.php HTTP/1.1");
      client.println("Host: 35.182.159.177");
      client.println("Content-Type: application/x-www-form-urlencoded");
      client.print("Content-Length: ");
      client.println(httpRequestData.length());
      client.println();
      client.print(httpRequestData);
      Serial.println("Connection and data passed!");
  }
}

void setup() {
 Serial.begin(115200);
 wm.setDarkMode(true);
 wm.setTitle("VegiTable Setup");
 wm.autoConnect();
 EEPROM.begin(32);
 dht.begin();
 ec.begin();
 pH.begin();
 sensors.begin();
}

void loop() {
  // run every 5 seconds
  delay(60*SECOND);

  readTemperature();
  readHumidity();
  readLight();
  readWaterLevel();
  readPH();
  readPPM();
  sendPHP();

}



