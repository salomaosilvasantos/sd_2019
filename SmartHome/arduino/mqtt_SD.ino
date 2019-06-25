#include <ESP8266WiFi.h> // Importa a Biblioteca ESP8266WiFi
#include <PubSubClient.h> // Importa a Biblioteca PubSubClient
#include "DHT.h"        // including the library of DHT11 temperature and humidity sensor
#include <Servo.h>

//defines:
//defines de id mqtt e tópicos para publicação e subscribe
#define TOPICO_SUBSCRIBE "MQTTCasaRecebe"     //tópico MQTT de escuta
#define TOPICO_PUBLISH   "MQTTCasaEnvia"    //tópico MQTT de envio de informações para Broker
                                                   //IMPORTANTE: recomendamos fortemente alterar os nomes
                                                   //            desses tópicos. Caso contrário, há grandes
                                                   //            chances de você controlar e monitorar o NodeMCU
                                                   //            de outra pessoa.

#define ID_MQTT  "CasaAutomacao"     //id mqtt (para identificação de sessão)
                               //IMPORTANTE: este deve ser único no broker (ou seja, 
                               //            se um client MQTT tentar entrar com o mesmo 
                               //            id de outro já conectado ao broker, o broker 
                               //            irá fechar a conexão de um deles).
#define DHTTYPE DHT11

//defines - mapeamento de pinos do NodeMCU
#define D0    16
#define D1    5
#define D2    4    // Define NodeMCU D2 pin  connect to Buzzer 
#define D3    0    // Define NodeMCU D3 pin to as temperature data pin of  DHT11
#define D4    2
#define D5    14
#define D6    12
#define D7    13  //pino de saida para acionamento da Lampada L1
#define D8    15
#define D9    3
#define D10   1

// WIFI
const char* SSID = "SalomaoAp";
const char* PASSWORD = "12345678";

// MQTT
//const char* BROKER_MQTT = "broker.mqtt-dashboard.com";
const char* BROKER_MQTT = "iot.eclipse.org";
int BROKER_PORT = 1883; // Porta do Broker MQTT


//Variáveis e objetos globais
String topic = "casa/quarto";

DHT dht(D3, DHTTYPE); 
//Variáveis e objetos globais
WiFiClient espClient; // Cria o objeto espClient
PubSubClient MQTT(espClient); // Instancia o Cliente MQTT passando o objeto espClient
char EstadoSaida = '0';  //variável que armazena o estado atual da saída
char EstadoSaidaLampada = 'F';
char EstadoSaidaAlarme = 'F';
char EstadoSaidaPorta = 'F';
Servo servoMotor;  // create servo object to control a servo

long lastMsg = 0;
char msg[50];
int value = 0;

//Prototypes
void InitOutput(void);
void initSerial();
void initWiFi();
void initMQTT();
void mqtt_callback(char* topic, byte* payload, unsigned int length);
void verificaConexoesWiFIEMQTT(void);
void reconectWiFi();
void reconnectMQTT();
void enviaEstadoOutputMQTT();

/* 
 *  Implementações das funções
 */
void setup() {
  InitOutput();
  initSerial();
  dht.begin();
  initWiFi();
  initMQTT();
}


//Função: inicializa o output em nível lógico baixo
//Parâmetros: nenhum
//Retorno: nenhum
void InitOutput(void){
    //IMPORTANTE: o Led já contido na placa é acionado (ou seja,
    //enviar HIGH para o output faz o Led acender / enviar LOW faz o Led apagar)
    pinMode(D7, OUTPUT);
    digitalWrite(D2, LOW); 
    pinMode(D2,OUTPUT);
    servoMotor.attach(D1);  // attaches the servo on pin D1 to the servo object    
    servoMotor.write(0); //POSIÇÃO DO SERVO FICA EM 0º (FECHADURA TRANCADA)
    
}

/*Função: inicializa comunicação serial com baudrate 115200 (para fins de monitorar no terminal serial 
 *        o que está acontecendo.
 *   Parâmetros: nenhum
 *   Retorno: nenhum
 */

void initSerial(){
    Serial.begin(115200);
}

//Função: inicializa e conecta-se na rede WI-FI desejada
//Parâmetros: nenhum
//Retorno: nenhum
void initWiFi(){
    delay(10);
    Serial.println("------Conexao WI-FI------");
    Serial.print("Conectando-se na rede: ");
    Serial.println(SSID);
    Serial.println("Aguarde ...");
         
    reconectWiFi();
}

//Função: inicializa parâmetros de conexão MQTT(endereço do 
//        broker, porta e seta função de callback)
//Parâmetros: nenhum
//Retorno: nenhum
void initMQTT() 
{
    MQTT.setServer(BROKER_MQTT, BROKER_PORT);   //informa qual broker e porta deve ser conectado
    MQTT.setCallback(mqtt_callback);            //atribui função de callback (função chamada quando qualquer informação de um dos tópicos subescritos chega)
}



//Função: função de callback 
//        esta função é chamada toda vez que uma informação de 
//        um dos tópicos subescritos chega)
//Parâmetros: nenhum
//Retorno: nenhum
void mqtt_callback(char* topic, byte* payload, unsigned int length){
  Serial.print("O Camando executado foi: ");
  Serial.print(topic);
  int p = payload[0]-'0';
  //int chk = DHT.read11(DHT11_PIN);
  // if MQTT comes a 0 message, LOW para a Lampada
  if(p==7){
    Serial.println(" Lampanda da Sala Desligada!] ");
    digitalWrite(D7, LOW);   //coloca saída em LOW para desligar a Lampada.
    EstadoSaidaLampada = 'F';
  } 
  
  if(p==1){
    digitalWrite(D7, HIGH);  //coloca saída em HIGH para ligar a Lampada 
    EstadoSaidaLampada = 'T';
    Serial.println(" Lampanda da Sala Ligada!] ");
  }
  
  
  if(p==8){
    noTone(D2);
    EstadoSaidaAlarme = 'F';
    Serial.println("Alarme Desligado! ");
  }
  if(p==3){
    tone(D2,2000);   
    Serial.println("Alarme Ligado! "); 
    
    EstadoSaidaAlarme = 'T';
    EstadoSaida = 'T';
    
    
    Serial.println("Alarme Desligado! ");
  }
  
  if(p==9){
    servoMotor.write(0); // SERVO GIRA A 82º (FECHADURA DESTRANCADA)
    EstadoSaidaPorta = 'F';
    Serial.println("Porta Fechada! ");
  }
  if(p==5){
    servoMotor.write(90); // SERVO GIRA A 82º (FECHADURA DESTRANCADA)
    EstadoSaidaPorta = 'T';
    Serial.println("Porta Aberta! ");
  }
  Serial.println();
} //end callback


//Função: verifica o estado das conexões WiFI e ao broker MQTT. 
//        Em caso de desconexão (qualquer uma das duas), a conexão
//        é refeita.
//Parâmetros: nenhum
//Retorno: nenhum
void verificaConexoesWiFIEMQTT(void){
    if (!MQTT.connected()) 
        reconnectMQTT(); //se não há conexão com o Broker, a conexão é refeita
     
     reconectWiFi(); //se não há conexão com o WiFI, a conexão é refeita
}

void reconnectMQTT(){
    while (!MQTT.connected()) 
    {
        Serial.print("* Tentando se conectar ao Broker MQTT: ");
        Serial.println(BROKER_MQTT);
        String clientId = "ESP8266Client-";
        clientId += String(random(0xffff), HEX);
        // Attempt to connect
        //if you MQTT broker has clientID,username and password
        //please change following line to    if (client.connect(clientId,userName,passWord))
        
        if (MQTT.connect(ID_MQTT)){
            Serial.println("Conectado com sucesso ao broker MQTT!");
            MQTT.subscribe(TOPICO_SUBSCRIBE); 
        } 
        else
        {
            Serial.println("Falha ao reconectar no broker.");
            Serial.println("Havera nova tentatica de conexao em 2s");
            delay(2000);
        }
    }
}


//Função: reconecta-se ao WiFi
//Parâmetros: nenhum
//Retorno: nenhum
void reconectWiFi(){
    //se já está conectado a rede WI-FI, nada é feito. 
    //Caso contrário, são efetuadas tentativas de conexão
    if (WiFi.status() == WL_CONNECTED)
        return;
         
    WiFi.begin(SSID, PASSWORD); // Conecta na rede WI-FI
     
    while (WiFi.status() != WL_CONNECTED) 
    {
        delay(100);
        Serial.print(".");
    }
   
    Serial.println();
    Serial.print("Conectado com sucesso na rede ");
    Serial.print(SSID);
    Serial.println("IP obtido: ");
    Serial.println(WiFi.localIP());
}

//Função: envia ao Broker o estado atual do output 
//Parâmetros: nenhum
//Retorno: nenhum
void enviaEstadoOutputMQTT(void){

     String msg="Lampada da Sala: ";
     msg= msg+ EstadoSaidaLampada;
     msg = msg+"; Alarme: " ;
     msg= msg + EstadoSaidaAlarme;
     msg = msg+"; Porta: " ;
     msg= msg + EstadoSaidaPorta;
     
     char message[100];
     msg.toCharArray(message,100);
     Serial.println(message);
    MQTT.publish(TOPICO_PUBLISH, message, true);

    Serial.println("- Estado da saida enviado ao broker!");
    
   
}


void loop() {

//garante funcionamento das conexões WiFi e ao broker MQTT
  verificaConexoesWiFIEMQTT();
  
  enviaEstadoOutputMQTT();
  
  long now = millis();
  
  // read DHT11 sensor every 1 seconds
  if (now - lastMsg > 2000) {
     lastMsg = now;
     float humidity = dht.readHumidity();
     float temperature = dht.readTemperature(); 
     //int chk = DHT.read11(DHT11_PIN);
     String msg="Temperatura: ";
     msg= msg+ temperature;
     msg = msg+" C; Umidade do Ar: " ;
     msg= msg + humidity;
     msg=msg+"%";
     char message[58];
     msg.toCharArray(message,58);
     Serial.println(message);
     //publish sensor data to MQTT broker
    MQTT.publish(String(topic + "/temperatura/dados").c_str(), message);
    MQTT.publish(String(topic + "/temperatura/celsius").c_str(), String(temperature).c_str(), true);
    MQTT.publish(String(topic + "/temperatura/fahrenheit").c_str(), String((temperature * 1.8) + 32).c_str(), true);
    MQTT.publish(String(topic + "/umidade").c_str(), String(humidity).c_str(), true);
  }
  delay(3000);
  MQTT.loop();
}
