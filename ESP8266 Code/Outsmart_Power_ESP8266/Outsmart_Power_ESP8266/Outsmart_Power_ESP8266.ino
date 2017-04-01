/*
Name:		Outsmart_Power_ESP8266.ino
Created:	3/4/2017 3:18 PM
Author:	Rene Moise Kwibuka
*/

//Includes.
#include <ESP8266WiFi.h>
#include <WiFiUDP.h>
#include <ArduinoJson.h>

//ACCESS POINT DEFINITIONS.
const char WIFI_AP_PASSWORD[] = "12345678";

//NETWORK DEFINITIONS
int localUdpPort = 2390;
int remoteIPPort = 4000;
WiFiUDP Udp;
bool connectedToHomeWifi = false;
bool connectedToPhoneApp = false;
String macID = ""; //This is used as the OutSmart ID.

// CONTROLLING PINS
int outlet1 = D0, outlet2 = D1, outlet3 = D2, outlet4 = D3;

// Status Variables
int status1 = 0, status2 = 0, status3 = 0, status4 = 0;


// the setup function runs once when you press reset or power the board
void setup() {
	// Open serial communications
	Serial.begin(9600);

	//Set up access point
	setupAPWiFi();
	
	Udp.begin(localUdpPort);
}

// the loop function runs over and over again forever
void loop() {

	int noBytes;
	noBytes = Udp.parsePacket();

	if (noBytes)
	{

		IPAddress remoteIP = Udp.remoteIP();
		String packetReceived = "";
		packetReceived = receiveUDPPacket(noBytes);
		Serial.println("Received a packet!");

		Serial.println(packetReceived);

		//JSON PROCESSING
		StaticJsonBuffer<200> jsonReceivedBuffer;

		JsonObject& root = jsonReceivedBuffer.parseObject(packetReceived);

		if (!root.success())
		{
			Serial.println("parseObject() failed");
			return;
		}


		String  type = root["type"];

		if (type == "CRED"){
			
			if (!connectedToHomeWifi){
				String  wifiName = root["s"];
				String  password = root["p"];

				//change strings to character arrays so we can pass them to wifi.begin().
				char wifiNameChar[30];
				char passwordChar[30];
				wifiName.toCharArray(wifiNameChar, wifiName.length() + 1);
				password.toCharArray(passwordChar, password.length() + 1);

				Serial.println(wifiNameChar);
				Serial.println(passwordChar);

				//Start STA Connection
				if (setupSTAMode(wifiNameChar, passwordChar))
				{
					connectedToHomeWifi = true;
					Serial.println(WiFi.localIP().toString());
				}
			}

			//Reserve memory space
			StaticJsonBuffer<200> jsonBufferSend;

			//Build object tree in memory
			JsonObject& root = jsonBufferSend.createObject();
			root["type"] = "CRED";
			root["ip"] = WiFi.localIP().toString();
			root["id"] = macID;
			char messageToSend[50];
			root.printTo(messageToSend, sizeof(messageToSend));
			String toSendData = messageToSend;
			
			//Send microcontroller's ip address.
			sendUDPPacket(toSendData, remoteIP, remoteIPPort);
		}
		else if (type == "REQU"){

			//Reserve memory space
			StaticJsonBuffer<200> jsonBufferSend;

			//Build object tree in memory
			JsonObject& root = jsonBufferSend.createObject();
			root["type"] = "REPL";
			root["data"] = "I am here!";
			root["id"] = String(macID);
			char messageToSend[50];
			root.printTo(messageToSend, sizeof(messageToSend));
			String toSendData = messageToSend;
			connectedToPhoneApp = true;
			//Send microcontroller's ip address.
			sendUDPPacket(toSendData, remoteIP, remoteIPPort);
		}

		//Else if the command from the user is to control the outlet. (turning on or off).
		else if (type == "CONT"){
			String toggle = root["toggle"];

			if (toggle == "on1")
			{		
				digitalWrite(outlet1, 1);   // switch on 
			}
			else if (toggle == "Off1")
			{
				
				digitalWrite(outlet1, 0);   // switch off

			}
			else if (toggle == "on2")
			{
				
				digitalWrite(outlet2, 1);   //// switch on 

			}
			else if (toggle == "off2")
			{
				
				digitalWrite(outlet2,0);   // switch off

			}
			else if (toggle == "on3")
			{
				digitalWrite(outlet3,1);   // // switch on 

			}
			else if (toggle == "off3")
			{
				digitalWrite(outlet3, 0);   // switch off

			}
			else if (toggle == "on4")
			{
				digitalWrite(outlet4, 1);   // switch on 

			}
			else if (toggle == "Off4")
			{
				digitalWrite(outlet4, 0);   // switch off

			}
		
			else{
				// Do nothing for now. We will send the updated status of all outlets anyways. 
				// We respond to this request as an acknowledgment too.
			}

			//Read the current status of all outlets.
			status1 = digitalRead(outlet1);
			status2 = digitalRead(outlet2);
			status3 = digitalRead(outlet3);
			status4 = digitalRead(outlet4);
			
			//Create a json to send.
			root["type"] = "CONT";
			root["s1"] = String(status1); root["s2"] = String(status2);
			root["s3"] = String(status3); root["s4"] = String(status4);
			root["id"] = String(macID);
			char messageToSend[200];
			root.printTo(messageToSend, sizeof(messageToSend));
			String toSendData = messageToSend;

			//send UDP Packet announcing status of every packet.
			sendUDPPacket(toSendData, remoteIP, remoteIPPort);
		}

	}
	char remoteIP[15] = "192.168.4.2";

	///sendUDPPacket("Hello", remoteIP, remoteIPPort);
	Serial.println(".");
	delay(1000);
}

void setupAPWiFi()
{
	WiFi.mode(WIFI_AP);	//Set Access Point Mode On.
	
	// Do a little work to get a unique-ish name. Append the
	// last two bytes of the MAC (HEX'd) to "Thing-":
	uint8_t mac[WL_MAC_ADDR_LENGTH];
	WiFi.softAPmacAddress(mac);
	macID = String(mac[WL_MAC_ADDR_LENGTH - 2], HEX) +
		String(mac[WL_MAC_ADDR_LENGTH - 1], HEX);
	macID.toUpperCase();
	String AP_NameString = "OutSmart " + macID;

	char AP_NameChar[AP_NameString.length() + 1];
	memset(AP_NameChar, 0, AP_NameString.length() + 1);

	for (int i = 0; i < AP_NameString.length(); i++)
		AP_NameChar[i] = AP_NameString.charAt(i);

	WiFi.softAP(AP_NameChar, WIFI_AP_PASSWORD);
}

/**
A method that receives remote packets.
*/
String receiveUDPPacket(int maxSize)
{
	char packetBuffer[512]; //buffer to hold incoming and outgoing packets
	// We've received a packet, read the data from it
	int len = Udp.read(packetBuffer, maxSize); // read the packet into the buffer
	if (len > 0) {
		packetBuffer[len] = 0;
	}
	Serial.print("String Received: ");
	//Serial.println(packetBuffer);
	String packet = packetBuffer;
	return packet;
}

//Connect to the wifi.
bool setupSTAMode(char wifiName[], char password[])
{
	WiFi.mode(WIFI_AP_STA);
	WiFi.begin(wifiName, password);

	bool connectedToHomeWifi = true;
	int tries = 0;
	while (WiFi.status() != WL_CONNECTED)
	{
		delay(500);
		Serial.print(".");
		tries++;
		if (tries > 30)
		{
			//Send UDP Packet to the phone app saying that the phone could not be connectedToHomeWifi.
			//Please try Again.
			//Go Back to listen.
			connectedToHomeWifi = false;
			break;
		}
	}

	return connectedToHomeWifi;
}

void sendUDPPacket(String messageToSend, IPAddress remoteIP, int port)
{
	char ReplyBuffer[256];
	messageToSend.toCharArray(ReplyBuffer, messageToSend.length() + 1);
	Udp.beginPacket(remoteIP, port);
	Udp.write(ReplyBuffer);
	Udp.endPacket();
}

void sendUDPPacket(String messageToSend, char remoteIP[15], int port)
{
	char ReplyBuffer[256];
	messageToSend.toCharArray(ReplyBuffer, messageToSend.length() + 1);
	Udp.beginPacket(remoteIP, port);
	Udp.write(ReplyBuffer);
	Udp.endPacket();
}