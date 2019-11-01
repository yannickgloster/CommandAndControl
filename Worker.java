/**
 *
 */
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

/**
 *
 * Worker class
 *
 * Prints the data given to it from the broker.
 *
 */
public class Worker extends Node {
    static final int DEFAULT_SRC_PORT = 50002;
    static final int DEFAULT_DST_PORT = 50001;
    static final String DEFAULT_DST_NODE = "broker";
    InetSocketAddress dstAddress;
    public boolean volunteerForWork;
    public boolean receivedWork;
    public boolean finishedWork;
    private String name;

    /**
     * Constructor
     *
     * Attempts to create socket at given port and create an InetSocketAddress for the destinations
     */
    Worker(String dstHost, int dstPort, int srcPort) {
        try {
            Scanner input = new Scanner(System.in);
            System.out.print("Please input name: ");
            name = input.next();
            System.out.print("\nWould you like to volunteer for work (y/n)? ");
            volunteerForWork = input.hasNext("y") || input.hasNext("y");
            System.out.println();

            receivedWork = false;
            finishedWork = false;
            dstAddress= new InetSocketAddress(dstHost, dstPort);
            socket= new DatagramSocket(srcPort);
            listener.go();
        }
        catch(java.lang.Exception e) {e.printStackTrace();}
    }


    /**
     * Checks the type of packet and responds appropriately.
     */
    public synchronized void onReceipt(DatagramPacket packet) {
        PacketContent content= PacketContent.fromDatagramPacket(packet);
        if(content.getType() == PacketContent.BROKERPACKET) {
            try {
                System.out.println("Received BrokerPacket: " + (BrokerPacket)content);
                receivedWork = true;
                volunteerForWork = false;
                finishedWork = false;

                sendWorkerPacket();
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            }

            System.out.println(((BrokerPacket) content).getData());

            try {
                receivedWork = false;
                volunteerForWork = true;
                finishedWork = true;
                sendWorkerPacket();
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println(content.toString());
        }
    }


    /**
     * Sender Method
     *
     */
    public synchronized void start() throws Exception {
        sendWorkerPacket();
        this.wait();
    }

    private void sendWorkerPacket() throws Exception {
        WorkerPacket wpacket = new WorkerPacket(volunteerForWork, receivedWork, finishedWork);
        DatagramPacket packet= null;

        System.out.println("Sending Worker Packet: " + wpacket);
        packet= wpacket.toDatagramPacket();
        packet.setSocketAddress(dstAddress);
        socket.send(packet);
    }


    /**
     * Test method
     *
     * Sends a packet to a given address
     */
    public static void main(String[] args) {
        try {
            (new Worker(DEFAULT_DST_NODE, DEFAULT_DST_PORT, DEFAULT_SRC_PORT)).start();
            System.out.println("Program completed");
        } catch(java.lang.Exception e) {e.printStackTrace();}
    }
}
