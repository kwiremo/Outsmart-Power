package com.outsmart.outsmartpower;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Name: PacketInformation Class
 *
 * Description: This class is a "holder" to pass two objects in one object.
 */

public class PacketInformation {

    //Contains the socket to be used
    private DatagramSocket socket;

    //Contains the packet to be sent or received
    private DatagramPacket packet;

    //Public constructor that sets the two fields
    public PacketInformation(DatagramSocket socketToUse, DatagramPacket packetToProcess){
        socket = socketToUse;
        packet = packetToProcess;
    }

    //Returns the socket field
    public DatagramSocket getSocket(){
        return socket;
    }

    //Returns the packet field
    public DatagramPacket getPacket(){
        return packet;
    }
}
