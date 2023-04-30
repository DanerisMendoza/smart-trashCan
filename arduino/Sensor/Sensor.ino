#include <WiFiManager.h>
#include <ESP8266HTTPClient.h>
#include <ESP8266WiFi.h>
#include <ArduinoJson.h>
#include <Ultrasonic.h>
#include <Servo.h>

//note: set wifi to private because it turn off firewall
String ipUrl = "192.168.1.5";
String insertUrl = "http://"+ipUrl+"/smart-trashCan/php/insertData.php";
String readUrl = "http://"+ipUrl+"/smart-trashCan/php/selectData.php";
String postPin = "post=smarttrashcan";
String mode = "";
StaticJsonDocument<200> doc;
//ultrasonic
const byte trigger_pin = D0;
const byte echo_pin = D1;
long   pulseTime ;

//rgb
byte red = D2;
byte green = D3;
byte blue = D4;
//ultrasonic 2
const byte trigger_pin2 = D5;
const byte echo_pin2 = D6;
long   pulseTime2;

//servo
Servo servo;

Ultrasonic ultrasonic1(trigger_pin, echo_pin);
Ultrasonic ultrasonic2(trigger_pin2, echo_pin2);

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
  // pinMode (trigger_pin, OUTPUT); 
  // pinMode (echo_pin, INPUT);
  //rgb
  pinMode(red, OUTPUT);
  pinMode(green, OUTPUT);
  pinMode(blue, OUTPUT);
   //ULTRA SONIC DISTANCE SENSOR 2
  // pinMode (trigger_pin2, OUTPUT); 
  // pinMode (echo_pin2, INPUT);
  //servo
  servo.attach(D7);
}
//note: make sure to not add space before and after &
void loop() {
  String data = "";
  // readDataOfServer();
  float distance = ultrasonic1.distanceRead();
  float distance2 = ultrasonic2.distanceRead();
  
  // Print the distances to the serial monitor
  Serial.print("Distance from sensor 1: "+ (String) distance) + "CM";
  Serial.print(" | ");
  Serial.println("Distance from sensor 2: " + (String) distance2 + "CM");

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
  delay(50); 
  //note: the servo bug trigger because it require more power therefore the other components is malfunctioning
  if(distance2 <= 10){
    Serial.println("Servo rotate to 90 deg");
    servo.write(180);
    delay(2000);
  }
  else if(distance2 > 10){
    servo.write(0);
  }
  delay(50); 
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
