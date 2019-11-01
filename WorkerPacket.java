import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class for packet content that represents the availability of a worker.
 *
 */
public class WorkerPacket extends PacketContent {

	public boolean volunteerForWork;
	public boolean receivedWork;
	public boolean finishedWork;

	/**
	 * Constructor that takes in information about a file.
	 * @param volunteerForWork true = ready to start work; false = not ready to start work
	 * @param receivedWork true = received work; false = have not received work
	 * @param finishedWork true = finished current work; false = have not finished current work
	 */
	WorkerPacket(boolean volunteerForWork, boolean receivedWork, boolean finishedWork) {
		type = WORKERPACKET;
		this.volunteerForWork = volunteerForWork;
		this.receivedWork = receivedWork;
		this.finishedWork = finishedWork;
	}

	/**
	 * Constructs an object out of a datagram packet.
	 * @param packet Packet that contains the availability of a worker.
	 */
	protected WorkerPacket(ObjectInputStream oin) {
		try {
			type= WORKERPACKET;
			volunteerForWork= oin.readBoolean();
			receivedWork= oin.readBoolean();
			finishedWork= oin.readBoolean();
		}
		catch(Exception e) {e.printStackTrace();}
	}

	/**
	 * Writes the content into an ObjectOutputStream
	 *
	 */
	protected void toObjectOutputStream(ObjectOutputStream oout) {
		try {
			oout.writeBoolean(volunteerForWork);
			oout.writeBoolean(receivedWork);
			oout.writeBoolean(finishedWork);
		}
		catch(Exception e) {e.printStackTrace();}
	}


	/**
	 * Returns the content of the packet as String.
	 *
	 * @return Returns the content of the packet as String.
	 */
	public String toString() {
		return "Volunteered for Work: " + volunteerForWork + " - Received Work: " + receivedWork + " - Finished Work: " + finishedWork;
	}

	/**
	 * Returns if the worker has volunteered for work.
	 *
	 * @return The status of receivedWork.
	 */
	public boolean getVolunteerForWork() {
		return volunteerForWork;
	}

	/**
	 * Returns if the worker has recieved work.
	 *
	 * @return The status of receivedWork.
	 */
	public boolean getReceivedWork() {
		return receivedWork;
	}

	/**
	 * Returns if the worker has finished work.
	 *
	 * @return The status of finishedWork.
	 */
	public boolean getFinishedWork() {
		return finishedWork;
	}
}
