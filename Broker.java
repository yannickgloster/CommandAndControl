import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Broker extends Node {
    static final int DEFAULT_PORT = 50001;
    private List<SocketAddress> allWorkers;
    private CommandPacket commandPacket;
    private BrokerPacket brokerPacket;
    private int count;
    private Thread workers;
    /*
     *
     */
    Broker(int port) {
        try {
            count = 0;
            commandPacket = null;
            brokerPacket = null;
            allWorkers = new ArrayList<>();
            socket= new DatagramSocket(port);
            listener.go();
        }
        catch(java.lang.Exception e) {e.printStackTrace();}
    }

    /**
     * Assume that incoming packets contain a String and print the string.
     */
    public synchronized void onReceipt(DatagramPacket packet) {
        try {
            PacketContent content = PacketContent.fromDatagramPacket(packet);

            if (content.getType() == PacketContent.WORKERPACKET) {
                System.out.println("Received WorkerPacket: " + (WorkerPacket)content);
                if (((WorkerPacket) content).getVolunteerForWork()) {
                    allWorkers.add(packet.getSocketAddress());
                    if (!((WorkerPacket) content).getFinishedWork()) {
                        System.out.println("Worker checked in for the first time.");
                    }
                } else {
                    allWorkers.remove(packet.getSocketAddress());
                }

                if (((WorkerPacket) content).getFinishedWork()) {
                    count--;
                }
            }
            if (content.getType() == PacketContent.COMMANDPACKET) {
                System.out.println("Received CommandPacket: " + (CommandPacket)content);
                commandPacket = (CommandPacket) content;
                brokerPacket = new BrokerPacket(((CommandPacket) commandPacket).getData());
                count = commandPacket.numTasks;

                DatagramPacket response;
                response = new AckPacketContent("OK - CommandPacket").toDatagramPacket();
                response.setSocketAddress(packet.getSocketAddress());
                socket.send(response);
                System.out.println("Sent AckPacket: " + response);

                workers = new Thread(() -> {
                    startWork();
                });
                workers.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void start() throws Exception {
            System.out.println("Waiting for contact");
            this.wait();
    }

    private synchronized void startWork() {
        while(count > 0) {
            Iterator itr = allWorkers.iterator();
            int countCopy = count;
            while (itr.hasNext()) {
                SocketAddress s = (SocketAddress) itr.next();
                if (countCopy > 0) {
                    try {
                        DatagramPacket workerTask;
                        workerTask = brokerPacket.toDatagramPacket();
                        workerTask.setSocketAddress(s);
                        socket.send(workerTask);
                        countCopy--;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                wait(2000);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Task Complete");
    }

    /*
     *
     */
    public static void main(String[] args) {
        try {
            (new Broker(DEFAULT_PORT)).start();
            System.out.println("Program completed");
        } catch(java.lang.Exception e) {e.printStackTrace();}
    }
}
