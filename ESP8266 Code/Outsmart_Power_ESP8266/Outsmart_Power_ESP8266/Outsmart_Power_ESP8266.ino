/*
Name:		Outsmart_Power_ESP8266.ino
Created:	3/4/2017 3:18 PM
Author:	Rene Moise Kwibuka
*/

//Includes.
#include <ESP8266WiFi.h>
#include <WiFiUDP.h>
#include <ArduinoJson.h>
#include <SoftwareSerial.h>
#include <FS.h>

//ACCESS POINT DEFINITIONS.
const char WIFI_AP_PASSWORD[] = "12345678";

//NETWORK DEFINITIONS
int localUdpPort = 2390;
int remoteIPPort = 4000;
IPAddress remoteIP;
WiFiUDP Udp;
bool connectedToHomeWifi = false;
bool connectedToPhoneApp = false;
String macID = ""; //This is used as the OutSmart ID.

// CONTROLLING PINS
int outlet1 = D1, outlet2 = D2, outlet3 = D5, outlet4 = D6;

// Status Variables
int status1 = 0, status2 = 0, status3 = 0, status4 = 0;

//File system variables
File f; //The file that will be opened and editied
bool firstTimeWriting = true; //If this is the first time we've written to the file
Dir dir; //Directory to find the file in

//Software Serial for serial monitor
SoftwareSerial mySerial(D3, D4);

//Time variables and constants
IPAddress timeServerIP; // time.nist.gov NTP server address
WiFiUDP timeUdp;
int localTimePort = 2391;
const char* ntpServerName = "time.nist.gov";
String epochString;
const int NTP_PACKET_SIZE = 48; // NTP time stamp is in the first 48 bytes of the message
byte packetBuffer[NTP_PACKET_SIZE]; //buffer to hold incoming and outgoing packets
unsigned long epoch; //Time value in UNIX format
unsigned long lastEpoch; //The last value in case we don't get time back from server
double current1 = 0, current2 = 0, current3 = 0, current4 = 0;
// the setup function runs once when you press reset or power the board
void setup() {
	// Open serial communications
	Serial.begin(9600);
	Serial.setTimeout(900);
	mySerial.begin(9600);

	//SPIFFS setup
	SPIFFS.begin();
	// Next lines have to be done ONLY ONCE!!!!!When SPIFFS is formatted ONCE you can comment these lines out!!
	//Serial.println("Please wait 30 secs for SPIFFS to be formatted");
	//SPIFFS.format();
	//Serial.println("Spiffs formatted");

	setUpPins();

	//Set up access point
	setupAPWiFi();
	
	Udp.begin(localUdpPort);
	//timeUdp.begin(localTimePort);

  //Alert that setup is complete
  mySerial.println("Ready!");
}

// the loop function runs over and over again forever
void loop() {

	if (!connectedToHomeWifi){
		if (setupSTAMode("eaglesnet", "")){
			connectedToHomeWifi = true;
		}
	}

	int noBytes;
	noBytes = Udp.parsePacket();

	if (noBytes)
	{

		remoteIP = Udp.remoteIP();
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

				//Start STA Connection if not already connected.
				if (setupSTAMode(wifiNameChar, passwordChar))
				{
					//if (firstTimeWriting){
						//getTimeFromInternet();
						//firstTimeWriting = false;
					//}
					connectedToHomeWifi = true;
					Serial.println("Internet is successfully connected!");
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
			root["id"] = macID;
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
				digitalWrite(outlet1, 0);   // switch on 
				Serial.println("turned 1 on");
			}
			else if (toggle == "off1")
			{
				
				digitalWrite(outlet1, 1);   // switch off
				Serial.println("turned 1 off");

			}
			else if (toggle == "on2")
			{
				digitalWrite(outlet2, 0);   //// switch on 
				Serial.println("turned 2 on");

			}
			else if (toggle == "off2")
			{
				
				digitalWrite(outlet2,1);   // switch off
				Serial.println("turned 2 off");
			}
			else if (toggle == "on3")
			{
				digitalWrite(outlet3,0);   // // switch on 

			}
			else if (toggle == "off3")
			{
				digitalWrite(outlet3, 1);   // switch off

			}
			else if (toggle == "on4")
			{
				digitalWrite(outlet4, 0);   // switch on 

			}
			else if (toggle == "off4")
			{
				digitalWrite(outlet4, 1);   // switch off

			}
		
			else{
				// Do nothing for now. We will send the updated status of all outlets anyways. 
				// We respond to this request as an acknowledgment too.
				Serial.println("Did not toggle any");
			}
		
			sendStatusUpdate();
		}

	}
	
	if (connectedToPhoneApp){
		sendPowerRecords();
		sendStatusUpdate();
	}

  //Get the power measurment data and store it
  //retrieveAndStorePowerInfo("f.txt");
  
	Serial.print("IP for remote host: ");
	Serial.println(remoteIP);
	Serial.println(WiFi.localIP().toString());

	if (connectedToHomeWifi){
		epoch++;
	}
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
	mySerial.print("String Received: ");
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

void setUpPins()
{
	// sets controlling pins as outputs
	pinMode(outlet1, OUTPUT);
	pinMode(outlet2, OUTPUT);
	pinMode(outlet3, OUTPUT);
	pinMode(outlet4, OUTPUT);

	digitalWrite(outlet1, 1);
	digitalWrite(outlet2, 1);
	digitalWrite(outlet3, 1);
	digitalWrite(outlet4, 1);
}

void sendStatusUpdate(){
	//Reserve memory space
	StaticJsonBuffer<200> jsonBufferSend;

	//Build object tree in memory
	JsonObject& root = jsonBufferSend.createObject();

	//Read the current status of all outlets.
	status1 = digitalRead(outlet1);
	status2 = digitalRead(outlet2);
	status3 = digitalRead(outlet3);
	status4 = digitalRead(outlet4);

	//Create a json to send.
	root["type"] = "CONT";
	root["s1"] = String(status1); root["s2"] = String(status2);
	root["s3"] = String(status3); root["s4"] = String(status4);
	root["id"] = macID;
	char messageToSend[200];
	root.printTo(messageToSend, sizeof(messageToSend));
	String toSendData = messageToSend;

	//send UDP Packet announcing status of every packet.
	sendUDPPacket(toSendData, remoteIP, remoteIPPort);
}

void sendPowerRecords(){
	  //Reserve memory space
	  StaticJsonBuffer<250> jsonBuffer;
	  //char temp[100]; //= "{\"t\":\"-7616\",\"v\":\"0.00\",\"c1\":\"0.05\",\"c2\":\"4.01\",\"c3\":\"8.66\",\"c4\":\"12.95\"}";
	  bool read = false;
	  String temp;
	  while ( Serial.available()) {
	    mySerial.println("Got data");
	    if ((temp=Serial.readStringUntil('\n')) != 0) {
	      
	      //Serial.print(i);
	
	      read = true;
	      //i++;
	    }
	    else
	      mySerial.println("No string");
	  }
	  
	  mySerial.println(temp);
	    //Parse object received from 328P
	  JsonObject& root = jsonBuffer.parseObject(temp);
	
	  if (!root.success())
	  {
	    mySerial.println("parseObject() failed");
	    temp = " ";
	    return;
	  }

	  root["t"] = String(1491685038);
	  root["id"] = macID;
	  root["type"] = "PORE";
	  char messageToSend[250];
	  root.printTo(messageToSend, sizeof(messageToSend));
	  String toSendData = messageToSend;
	  //Serial.println(toSendData);

	  //send UDP Packet
	  sendUDPPacket(toSendData, remoteIP, remoteIPPort);
}

//Method for getting NTP Time from internet
void getTimeFromInternet()
{
  //get a random server from the pool
  WiFi.hostByName(ntpServerName, timeServerIP);

  sendNTPpacket(timeServerIP); // send an NTP packet to a time server

    // wait to see if a reply is available
    delay(175);

  int cb = timeUdp.parsePacket();
  if (!cb) {
    mySerial.println("Time fail");
  }
  else {
    // We've received a packet, read the data from it
    timeUdp.read(packetBuffer, NTP_PACKET_SIZE); // read the packet into the buffer

                         //the timestamp starts at byte 40 of the received packet and is four bytes,
                         // or two words, long. First, esxtract the two words:
    unsigned long highWord = word(packetBuffer[40], packetBuffer[41]);
    unsigned long lowWord = word(packetBuffer[42], packetBuffer[43]);

    // combine the four bytes (two words) into a long integer
    // this is NTP time (seconds since Jan 1 1900):
    unsigned long secsSince1900 = highWord << 16 | lowWord;

    // now convert NTP time into everyday time:
    // Unix time starts on Jan 1 1970. In seconds, that's 2208988800:
    const unsigned long seventyYears = 2208988800UL;
    lastEpoch = epoch;
    epoch = secsSince1900 - seventyYears;
    
    epochString = String(epoch);
    mySerial.println(epoch);
  }
}

// send an NTP request to the time server at the given address (for time retreival)
unsigned long sendNTPpacket(IPAddress& address)
{
  ///Serial.println("sending NTP packet...");
  // set all bytes in the buffer to 0
  memset(packetBuffer, 0, NTP_PACKET_SIZE);
  // Initialize values needed to form NTP request
  // (see URL above for details on the packets)
  packetBuffer[0] = 0b11100011;   // LI, Version, Mode
  packetBuffer[1] = 0;     // Stratum, or type of clock
  packetBuffer[2] = 6;     // Polling Interval
  packetBuffer[3] = 0xEC;  // Peer Clock Precision
               // 8 bytes of zero for Root Delay & Root Dispersion
  packetBuffer[12] = 49;
  packetBuffer[13] = 0x4E;
  packetBuffer[14] = 49;
  packetBuffer[15] = 52;

  // all NTP fields have been given values, now
  // you can send a packet requesting a timestamp:
  timeUdp.beginPacketMulticast(address, 123,WiFi.localIP()); //NTP requests are to port 123
  timeUdp.write(packetBuffer, NTP_PACKET_SIZE);
  timeUdp.endPacket();
}
//
////Method to get power data from ATMega 328P and store it
//void retrieveAndStorePowerInfo(String path){
//
////Get time from internet if we're connected to the internet
//  if(connectedToHomeWifi){
//    getTimeFromInternet();
//
//    //If time value failed to update it, increment it
//    if (lastEpoch == epoch) {
//      epoch += 1;
//      lastEpoch += 1;
//      epochString = (String)epoch;
//    }
//  }
//
//  //Open the file for writing if first time opening
//  if (firstTimeWriting) {
//    f = SPIFFS.open(path, "w");
//    firstTimeWriting = false;
//  }
//  //Open the file for appending if first time opening
//  else {
//    // open file for writing
//    f = SPIFFS.open(path, "a");
//  }
//
//  if (!f) {
//    mySerial.println("file open failed");
//  }
//  
//  mySerial.println("====== Writing to SPIFFS file =========");
//  // write 10 strings to file
//
//  //Reserve memory space
//  StaticJsonBuffer<200> jsonBuffer;
//  //char temp[100]; //= "{\"t\":\"-7616\",\"v\":\"0.00\",\"c1\":\"0.05\",\"c2\":\"4.01\",\"c3\":\"8.66\",\"c4\":\"12.95\"}";
//  int incomingSerialDataIndex = 0;
//
//
//  bool read = false;
//  String temp;
//  while ( Serial.available()) {
//    mySerial.println("Got data");
//    if ((temp=Serial.readStringUntil('\n')) != 0) {
//      
//      //Serial.print(i);
//
//      read = true;
//      //i++;
//    }
//    else
//      mySerial.println("No string");
//  }
//  
//  mySerial.println(temp);
//    //Parse object received from 328P
//  JsonObject& root = jsonBuffer.parseObject(temp);
//
//  if (!root.success())
//  {
//    mySerial.println("parseObject() failed");
//    temp = " ";
//    return;
//  }
//
//  // Received in this form: {"t":"-7616","v":"0.00","c1":"0.05","c2":"4.01","c3":"8.66","c4":"12.95"}
//  float        current1 = root["c1"];
//  float        current2 = root["c2"];
//  float        current3 = root["c3"];
//  float        current4 = root["c4"];
//  float        voltage = root["v"];
//
//  //Store the power records
//  f.println(";V:" + (String)voltage + ",C1:" + (String)current1 + ",C2:" + (String)current2
//    + ",C3:" + (String)current3 + ",C4:" + (String)current4 + ",T:" + epochString);
//
//  /*StaticJsonBuffer<200> jsonBuffer1;
//  JsonObject& root1 = jsonBuffer1.createObject();
//  root1["v"] = voltage + .01;
//  root1["c1"] = current1 + .01;
//  root1["c2"] = current2 + .01;
//  root1["c3"] = current3 + .01;
//  root1["c4"] = current4 + .01;
//  root1["t"] = epochString;
//
//  root1.printTo(temp);*/
//  
//  //Close the file so it can be accessed by other methods
//  f.close();
//}

