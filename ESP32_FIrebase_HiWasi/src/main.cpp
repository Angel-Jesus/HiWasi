#include <Arduino.h>
#include "secret.h"
#include <DHT.h>

#if defined(ESP32)
#include <WiFi.h>
#include <FirebaseESP32.h>
#elif defined(ESP8266)
#include <WiFi8266.h>
#include <FirebaseESP8266.h>
#endif

#include <addons/TokenHelper.h>
#include <addons/RTDBHelper.h>

//Definir los pines 
#define DHT_PIN 26
#define MQ2 35
#define MQ7 34
#define LED_BLINK 5

//Definir los paths de DB
#define path_led "/Led1"
#define path_temp "/temp"
#define path_humd "/humd"
#define path_token "/token"
#define path_CO "/CO"
#define path_GLP "/GLP"
#define path_alarm "/alarm"

//Definir lo sensores 
#define DHTTYPE DHT11

//DHT11
DHT dht(DHT_PIN,DHTTYPE);

//Definir objectos Firebase data
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

//Definir variables globales
unsigned long sendDataPrevMillis = 0;
unsigned long sensorPrevMillis = 0;
String stateLed = "0";
String stateLedPrev = "0";
String temp = "";
String humd = "";
String token_id = "";
String CO = "";
String GLP = "";
String alarm_state = "";

//Declaracion de funciones
void initialPine();
void sendMenssage();
float temperature();
double CO_Data();
double GLP_Data();

void setup() {

  //Configuracion de los baudios de carga del esp32
  Serial.begin(115200);
  initialPine();
  delay(10);

  //---Funcion para configurar el wifi---------
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED){
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();
  //-------------------------------------------

  //Configuracion para el acceso a la base de datos de Firebase------
  Serial.printf("Firebase Client v%s\n\n", FIREBASE_CLIENT_VERSION);

  config.api_key = API_KEY;
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;
  config.database_url = DATABASE_URL;
  Firebase.reconnectWiFi(true);
  fbdo.setResponseSize(2048);
  config.token_status_callback = tokenStatusCallback;
  config.max_token_generation_retry = 5;
  Firebase.begin(&config, &auth);

  if(Firebase.RTDB.getString(&fbdo,path_token)){
  token_id = fbdo.stringData();
  }

  fbdo.fcm.begin(FIREBASE_FCM_SERVER_KEY);
  fbdo.fcm.addDeviceToken(token_id);
  fbdo.fcm.setPriority("high");
  //------------------------------------------------------------

  //Funcion de inicializar el puerto dht
  dht.begin();
}

void loop() {

  if( millis() - sensorPrevMillis > 2000 || sensorPrevMillis == 0){
    sensorPrevMillis = millis();
    /*
    float t = temperature();
    double CO = CO_Data();
    double GLP = GLP_Data();
    */
    temperature();
    CO_Data();
    GLP_Data();
    /*
    if(Firebase.RTDB.getString(&fbdo,path_alarm)){
      alarm_state = fbdo.stringData();
    }
    if((t > 35 || CO > 200 || GLP > 500) && alarm_state == "no"){
      Firebase.RTDB.setString(&fbdo,path_alarm,"si");
      sendMenssage();
    }*/

  }

  if(Firebase.ready() && (millis() - sendDataPrevMillis > 200 || sendDataPrevMillis == 0)){
    sendDataPrevMillis = millis();
    //Todo el codigo de lectura y escritura a la base de datos--
    if(Firebase.RTDB.getString(&fbdo,path_led)){
      stateLed = fbdo.stringData();
      if(stateLed != stateLedPrev){

        if(stateLed == "0"){
          digitalWrite(LED_BLINK,LOW);
        }else{
          digitalWrite(LED_BLINK,HIGH);
        }
        stateLedPrev = stateLed;
      }
    }else{
      Serial.println(fbdo.errorReason());
    }
    
  }
  //-------------------------------------------
 
}

void initialPine(){
  pinMode(LED_BLINK, OUTPUT);
  digitalWrite(LED_BLINK, LOW);
}

float temperature(){
  float t = dht.readTemperature();
  temp.concat(t);
  humd.concat(dht.readHumidity());
  //Temperatura  Humedad    
  Firebase.RTDB.setString(&fbdo,path_temp,temp);
  Firebase.RTDB.setString(&fbdo,path_humd,humd);
  Serial.print("temp: ");
  Serial.println(temp);
  Serial.print("humd: ");
  Serial.println(humd);
  //Limpiar el dato
  temp = "";
  humd = "";
  return t;
}

double CO_Data(){
  int adc_MQ7 = analogRead(MQ7);
  float voltajeCO = adc_MQ7*(3.3/4095.0); //Convertir la lectura en valor de voltaje
  float RS_CO = 1000*((3.3-voltajeCO)/voltajeCO);
  double CO_value = 97.076*pow(RS_CO/810,-1.532); //Colocar el valor de RoCO, donde pow(RS_CO/Ro_CO,-0.652)
  CO.concat(CO_value);
  Serial.print("CO: ");
  Serial.println(voltajeCO);
  //Send Data to DataBase
  Firebase.RTDB.setString(&fbdo,path_CO,CO);
  CO = "";
  return CO_value;
}

double GLP_Data(){
  int adc_MQ2 = analogRead(MQ2);
  float voltajeGLP = adc_MQ2*(5/4095.0); //Convertir la lectura en valor de voltaje
  float RS_GLP = 1000*((5-voltajeGLP)/voltajeGLP);
  double GLP_value = 578.81*pow(RS_GLP/1360,-2.102); //Colocar el valor de RoCO, donde pow(RS_CO/Ro_GLP,-0.652)
  GLP.concat(GLP_value);
  Serial.print("GLP: ");
  Serial.println(voltajeGLP);
  //Send Data to DataBase
  Firebase.RTDB.setString(&fbdo,path_GLP,GLP);
  GLP = "";
  return GLP_value;
}

void sendMenssage(){
  Serial.println("------------------------------------");
  Serial.println("Send Firebase Cloud Messaging...");
  fbdo.fcm.setNotifyMessage("Alerta", "Se detecto fuga de Gas o Humo");

  if (Firebase.sendMessage(fbdo, 0))//send message to recipient index 0
    {
        Serial.println("PASSED");
        Serial.println(fbdo.fcm.getSendResult());
        Serial.println("------------------------------------");
        Serial.println();
    }
    else
    {
        Serial.println("FAILED");
        Serial.println("REASON: " + fbdo.errorReason());
        Serial.println("------------------------------------");
        Serial.println();
    }
    fbdo.fcm.clearNotifyMessage();
    fbdo.fcm.clearDataMessage();
}