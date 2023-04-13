#include <WiFiManager.h>
#include <ESP8266HTTPClient.h>
#include <ESP8266WiFi.h>
#include <ArduinoJson.h>

//note: set wifi to private because it turn off firewall
String ipUrl = "192.168.1.4";
String insertUrl = "http://"+ipUrl+"/smart_trashCan/php/insertData.php";
String readUrl = "http://"+ipUrl+"/smart_trashCan/php/selectData.php";
String postPin = "post=smarttrashcan";
String mode = "";
const byte led = D1;
StaticJsonDocument<200> doc;

void setup() {
  Serial.begin(9600);
  WiFiManager wm;
  pinMode(led,OUTPUT);
  if(wm.autoConnect("Sensor","password")) {
      Serial.println("Connected");
  } 
  else { 
      Serial.println("Connection failed");
  }
 
}

void loop() {
  String value = "working";
  String data = "post=smarttrashcan & data=" + (String) value;
  sendDataToServer(data);
  readDataOfServer();
  if(mode == "ON"){
    digitalWrite(led,HIGH);
  }
  else{
    digitalWrite(led,LOW);
  }
  delay(1000);
}

void sendDataToServer(String data) {
  WiFiClient wifiClient;
  HTTPClient http;
  http.begin(wifiClient, insertUrl);
  http.addHeader("Content-Type", "application/x-www-form-urlencoded");
  int httpCode = http.POST(data);
  String response = http.getString();
  // Serial.println(httpCode);  //show your http response status
  // Serial.println(response);  //show your echo
  http.end();
}

void readDataOfServer() {
  WiFiClient wifiClient;
  HTTPClient http;
  http.begin(wifiClient, readUrl);
  http.addHeader("Content-Type", "application/x-www-form-urlencoded");
  int httpCode = http.POST(postPin);
  String response = http.getString();
  deserializeJson(doc, response);
  response = doc["mode"].as<String>();
  mode = response;
  Serial.println(response);
  http.end();
}
