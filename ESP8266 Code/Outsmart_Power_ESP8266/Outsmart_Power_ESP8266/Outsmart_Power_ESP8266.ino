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

//UDP DEFINITIONS
int udpPort = 2390;
WiFiUDP Udp;
bool connected = false;
String macID = ""; //This is used as the OutSmart ID.


// the setup function runs once when you press reset or power the board
void setup() {
	// Open serial communications
	Serial.begin(9600);

	//Set up access point
	setupAPWiFi();
	
	Udp.begin(udpPort);
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
		StaticJsonBuffer<200> jsonBuffer;

		JsonObject& root = jsonBuffer.parseObject(packetReceived);

		if (!root.success())
		{
			Serial.println("parseObject() failed");
			return;
		}


		String  type = root["type"];

		if (type == "CRED"){
			
			if (!connected){
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
					connected = true;
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
			sendUDPPacket(toSendData, remoteIP, 4000);
		}
	}
	char remoteIP[15] = "192.168.4.2";

	///sendUDPPacket("Hello", remoteIP, 4000);
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

	bool connected = true;
	int tries = 0;
	while (WiFi.status() != WL_CONNECTED)
	{
		delay(500);
		Serial.print(".");
		tries++;
		if (tries > 30)
		{
			//Send UDP Packet to the phone app saying that the phone could not be connected.
			//Please try Again.
			//Go Back to listen.
			connected = false;
			break;
		}
	}

	return connected;
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