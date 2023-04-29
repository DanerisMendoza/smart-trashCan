#include <WiFiManager.h>
#include <ESP8266HTTPClient.h>
#include <ESP8266WiFi.h>
#include <ArduinoJson.h>

//note: set wifi to private because it turn off firewall
String ipUrl = "192.168.1.6";
String insertUrl = "http://"+ipUrl+"/smart-trashCan/php/insertData.php";
String readUrl = "http://"+ipUrl+"/smart-trashCan/php/selectData.php";
String postPin = "post=smarttrashcan";
String mode = "";
StaticJsonDocument<200> doc;
//ultrasonic
const byte trigger_pin = D0;
const byte echo_pin = D1;
long   pulseTime ;
double distance; 
//rgb
byte red = D2;
byte green = D3;
byte blue = D4;

void setup() {
  Serial.begin(9600);
  WiFiManager wm;
  if(wm.autoConnect("Sensor","password")) {
      Serial.println("Connected");
  } 
  else { 
      Serial.println("Connection failed");
  }
  //ULTRA SONIC DISTANCE SENSOR
  pinMode (trigger_pin, OUTPUT); 
  pinMode (echo_pin, INPUT);
  //rgb
  pinMode(red, OUTPUT);
  pinMode(green, OUTPUT);
  pinMode(blue, OUTPUT);
 
}
//note: make sure to not add space before and after &
void loop() {
  String data = "";
  readDataOfServer();
  digitalWrite (trigger_pin, HIGH);
  delayMicroseconds (10);
  digitalWrite (trigger_pin, LOW);
  pulseTime  = pulseIn(echo_pin, HIGH);
  distance = double(pulseTime  * 0.034 / 2.0);
  printStatus(distance);

  if(distance <= 5){
    data = "post=smarttrashcan&data=3";
    // red
    digitalWrite(red, HIGH);
    digitalWrite(green, LOW);
    digitalWrite(blue, LOW);
  }
  else if(distance < 25 && distance > 5){
    data = "post=smarttrashcan&data=2";
    //blue
    digitalWrite(red, LOW);
    digitalWrite(green, LOW);
    digitalWrite(blue, HIGH);
  }
  else{
    data = "post=smarttrashcan&data=1";
    //green
    digitalWrite(red, LOW);
    digitalWrite(green, HIGH);
    digitalWrite(blue, LOW);
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
