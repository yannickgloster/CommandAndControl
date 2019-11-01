/**
 *
 */
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

/**
 *
 * Command and Control class
 *
 * An instance accepts user input and sends a task to the broker.
 *
 */
public class CommandAndControl extends Node {
    static final int DEFAULT_SRC_PORT = 50000;
    static final int DEFAULT_DST_PORT = 50001;
    static final String DEFAULT_DST_NODE = "broker"; //Name of the container
    InetSocketAddress dstAddress;
    String description;
    int numTasks;
    String data;
    boolean brokerReceived;

    /**
     * Constructor
     *
     * Attempts to create socket at given port and create an InetSocketAddress for the destinations.
     */
    CommandAndControl(String dstHost, int dstPort, int srcPort) {
        try {
            Scanner input = new Scanner(System.in);
            System.out.println("Please input the message you would liked printed:");
            data = input.next();
            System.out.println("How many times would you like \n" + "-> " + data + "\nprinted?");
            numTasks = input.nextInt();

            brokerReceived = false;
            description = "print";
            dstAddress= new InetSocketAddress(dstHost, dstPort);
            socket= new DatagramSocket(srcPort);
            listener.go();
        }
        catch(java.lang.Exception e) {e.printStackTrace();}
    }


    /**
     * Checks the data from the packet coming from the broker.
     */
    public synchronized void onReceipt(DatagramPacket packet) {
        PacketContent content= PacketContent.fromDatagramPacket(packet);
        if(!((AckPacketContent)content).toString().equals("complete")) {
            System.out.println(content.toString());
            brokerReceived = true;
        }
        else{
            this.notify();
        }
    }


    /**
     * Sender Method
     *
     */
    public synchronized void start() throws Exception {
        int countSent = 0;
        while(!brokerReceived) {
            CommandPacket cpacket = new CommandPacket(numTasks, data);
            DatagramPacket packet= null;
            System.out.println("Sending packet w/ work | Attempt: " + countSent);
            packet= cpacket.toDatagramPacket();
            packet.setSocketAddress(dstAddress);
            socket.send(packet);
            System.out.println("Packet sent");
            this.wait(2000);
            countSent++;
        }
    }

    /**
     * Test method
     *
     * Sends a packet to a given address.
     */
    public static void main(String[] args) {
        try {
            (new CommandAndControl(DEFAULT_DST_NODE, DEFAULT_DST_PORT, DEFAULT_SRC_PORT)).start();
            System.out.println("Program completed");
        } catch(java.lang.Exception e) {e.printStackTrace();}
    }
}
