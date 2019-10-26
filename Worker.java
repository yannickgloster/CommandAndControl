/**
 *
 */
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.io.File;
import java.io.FileInputStream;

/**
 *
 * Worker class
 *
 * Does Task
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

    /**
     * Constructor
     *
     * Attempts to create socket at given port and create an InetSocketAddress for the destinations
     */
    Worker(String dstHost, int dstPort, int srcPort) {
        try {
            volunteerForWork = true;
            receivedWork = false;
            finishedWork = false;
            dstAddress= new InetSocketAddress(dstHost, dstPort);
            socket= new DatagramSocket(srcPort);
            listener.go();
        }
        catch(java.lang.Exception e) {e.printStackTrace();}
    }


    /**
     * Assume that incoming packets contain a String and print the string.
     */
    public synchronized void onReceipt(DatagramPacket packet) {
        PacketContent content= PacketContent.fromDatagramPacket(packet);
        if(content.getType() == PacketContent.BROKERPACKET) {
            try {
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

        //System.out.println("Sending packet w/ availability for work");
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
