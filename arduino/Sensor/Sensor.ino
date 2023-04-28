#include <WiFiManager.h>
#include <ESP8266HTTPClient.h>
#include <ESP8266WiFi.h>
#include <ArduinoJson.h>

//note: set wifi to private because it turn off firewall
String ipUrl = "192.168.1.4";
String insertUrl = "http://"+ipUrl+"/smart-trashCan/php/insertData.php";
String readUrl = "http://"+ipUrl+"/smart-trashCan/php/selectData.php";
String postPin = "post=smarttrashcan";
String mode = "";
const byte led = D1;
StaticJsonDocument<200> doc;
//ultrasonic
const byte trigger_pin = D2;
const byte echo_pin = D3;
long   pulseTime ;
double distance; 

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
  //ULTRA SONIC DISTANCE SENSOR
  pinMode (trigger_pin, OUTPUT); 
  pinMode (echo_pin, INPUT);
 
}
//note: make sure to not add space before and after &
void loop() {
  String data = "";
  readDataOfServer();
  if(mode == "ON"){
    digitalWrite(led,HIGH);
  }
  else{
    digitalWrite(led,LOW);
  }
  digitalWrite (trigger_pin, HIGH);
  delayMicroseconds (10);
  digitalWrite (trigger_pin, LOW);
  pulseTime  = pulseIn(echo_pin, HIGH);
  distance = double(pulseTime  * 0.034 / 2.0);
  printStatus(distance);
  // delay(1000);
  if(distance <= 5){
    data = "post=smarttrashcan&data=3";
  }
  else if(distance < 25 && distance > 5){
    data = "post=smarttrashcan&data=2";
  }
  else{
    data = "post=smarttrashcan&data=1";
  }
  sendDataToServer(data);
}

void printStatus(double distance){
  Serial.print ("Distance= ");              
  Serial.print (distance);   
  Serial.println("cm");
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
  // Serial.println(response);
  http.end();
}
