import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class for packet content that represents the information from the CommandAndControl
 *
 */
public class CommandPacket extends PacketContent {

    int numTasks;
    String data;

    /**
     * Constructor that takes in information from the CommandAndControl.
     * @param data String to be printed.
     * @param numTasks The Number of Tasks.
     */
    CommandPacket(int numTasks, String data) {
        type= COMMANDPACKET;
        this.numTasks= numTasks;
        this.data= data;
    }

    /**
     * Constructs an object out of a datagram packet.
     * @param packet Packet that contains information from the CommandAndControl.
     */
    protected CommandPacket(ObjectInputStream oin) {
        try {
            type= COMMANDPACKET;
            numTasks= oin.readInt();
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
            oout.writeInt(numTasks);
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
        return "numTasks: " + numTasks + " - Data: " + data;
    }

    /**
     * Returns the file numTasks contained in the packet.
     *
     * @return Returns the file numTasks contained in the packet.
     */
    public int getNumTasks() {
        return numTasks;
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
