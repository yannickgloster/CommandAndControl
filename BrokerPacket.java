import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class for packet content that represents a broker packet
 *
 */
public class BrokerPacket extends PacketContent {

    String data;

    /**
     * Constructor that takes a String from a Broker.
     * @param data Initial description.
     */
    BrokerPacket(String data) {
        type= BROKERPACKET;
        this.data= data;
    }

    /**
     * Constructs an object out of a datagram packet.
     * @param packet Packet that contains information about a file.
     */
    protected BrokerPacket(ObjectInputStream oin) {
        try {
            type= BROKERPACKET;
            data= oin.readUTF();
        }
        catch(Exception e) {e.printStackTrace();}
    }

    /**
     * Writes the content into an ObjectOutputStream
     *
     */
    protected void toObjectOutputStream(ObjectOutputStream oout) {
        try {
            oout.writeUTF(data);
        }
        catch(Exception e) {e.printStackTrace();}
    }


    /**
     * Returns the content of the packet as String.
     *
     * @return Returns the content of the packet as String.
     */
    public String toString() {
        return "Data: " + data;
    }

    /**
     * Returns the data.
     *
     * @return Return the data.
     */
    public String getData() {
        return data;
    }
}
