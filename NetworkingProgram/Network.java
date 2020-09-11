
/**
 * Network - Project 2 - RDT 3.0 - Jacob Leiner COP 5518 Summer 2020
 * 
 * This program simulates a network by accepting packets on its socket, creating a thread to read the packet's message, and then determining where to forward the packet from
 * the fields parsed from the received packet's message after setting flags based off of whether a packet meets the threshold for errors, delays, and lost 
 * packets
 * 
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

public class Network {
	static final int BUFFER_SIZE = 256;
	static int networkPort;
	static int lostPercent;
	static int delayedPercent;
	static int errorPercent;
	static DatagramSocket networkSocket;
	static int numPacketsReceived = 0;
	static double numofMillisTotalToProcess;
	
	public Network() {	
	}
	
	/**
	 * creates a socket
	 * 
	 * @param port
	 */
	private static void createSocket(int port) {
		try {
			networkSocket = new DatagramSocket(networkPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * receives a packet
	 * 
	 * @return DatagramPacket
	 */
	public static DatagramPacket receivePacket() {
		byte[] buffer = new byte[BUFFER_SIZE];
		DatagramPacket newDatagramPacket = new DatagramPacket(buffer, BUFFER_SIZE);
		try {
			networkSocket.receive(newDatagramPacket);
		} catch (IOException ioex) {
			System.out.println("unable to receive message");
			return null;
		}

		System.out.println(
				"Received a packet from " + newDatagramPacket.getAddress() + ", " + newDatagramPacket.getPort());
		return newDatagramPacket;
	}

	public static void main(String[] args) {
		// create variables from command line

		Network.networkPort = Integer.parseInt(args[0]);
		Network.lostPercent = Integer.parseInt(args[1]);
		Network.delayedPercent = Integer.parseInt(args[2]);
		Network.errorPercent = Integer.parseInt(args[3]);
		System.out.println("Network running on port " + networkPort);

		createSocket(networkPort);

		while (true) {
			try {
				networkSocket.setSoTimeout(0);
			} catch (SocketException e) {
				System.out.println("network socket somehow timed out");
				e.printStackTrace();
			}
			DatagramPacket packet = receivePacket();
			ReceivedPacket receivedPacket = new ReceivedPacket(packet);
			receivedPacket.start();
		}

	}

	private static class ReceivedPacket extends Thread {
		private static String sourceIP;
		private static int sourcePort;
		private static String destIP;
		private static int destPort;
		private static DatagramPacket receivedPacket;
		private static DatagramPacket packetToForward;
		private static boolean hasError;
		private static boolean hasDelay;
		private static boolean isLost;
		private static Random random;
		private static boolean fromSender;
		private static String message;
		private static double receivedTime;

		public ReceivedPacket(DatagramPacket packet) {
			receivedPacket = packet;
			random = new Random();
			unpackMessageFields(receivedPacket);
			hasError = random.nextInt(101) < Network.errorPercent;
			hasDelay = random.nextInt(101) < Network.delayedPercent;
			isLost = random.nextInt(101) < Network.lostPercent;
			int receivedPacketPort = packet.getPort();
			fromSender = (receivedPacketPort == sourcePort);
			System.out.println("fromSender equals: " + fromSender);
			receivedTime = System.currentTimeMillis();
			Network.numPacketsReceived++;
		}

		@Override
		public void run() {
			System.out.println("run method has started");
			// create a packet to forward
			if (fromSender) {
				packetToForward = createPacketToForward(message, destIP, destPort);
			} else {
				packetToForward = createPacketToForward(message, sourceIP, sourcePort);
			}

			if (packetToForward != null) {
				forwardPacket(packetToForward);
				if (fromSender) {
					System.out.println("Forwarded packet to address " + destIP + " on port " + destPort);
				} else {
					System.out.println("Forwarded packet to address " + sourceIP + " on port " + sourcePort);
				}
				Network.numofMillisTotalToProcess += (System.currentTimeMillis() - receivedTime);
				System.out.println("Network received and forwarded this packet in " + (System.currentTimeMillis() - receivedTime) + " milliseconds");
				System.out.println("Network has received and forwarded " + Network.numPacketsReceived + " packets now.");
				System.out.println("Average time to process and forward a packet is now " + 
						(Network.numofMillisTotalToProcess / Network.numPacketsReceived) + " milliseconds to process.");
			}
		}

		/**
		 * this method takes the packet it receives, and parses its message to determine
		 * where the received packet should be sent
		 * 
		 * @param receivedPacket
		 */
		private void unpackMessageFields(DatagramPacket receivedPacket) {
			message = new String(receivedPacket.getData());
			System.out.println("message received from " + receivedPacket.getAddress() + " is " + message);
			sourceIP = message.substring(0, message.indexOf('\\', 0));
			sourcePort = Integer.parseInt(message.substring(16, message.indexOf('\\', 16)));
			destIP = message.substring(22, message.indexOf('\\', 22));
			destPort = Integer.parseInt(message.substring(38, message.indexOf('\\', 38)));
			System.out.println("sourceIP: " + sourceIP);
			System.out.println("sourcePort: " + sourcePort);
			System.out.println("destIP: " + destIP);
			System.out.println("destPort: " + destPort);
		}

		/**
		 * creates a datagram packet
		 * 
		 * @param receivedPacket
		 * @param hostAddress
		 * @param port
		 * @return
		 */
		private DatagramPacket createPacketToForward(String message, String hostAddress, int port) {
			// manipulate packet to forward based on network class parameters set by user upon running program
			String messageToSend = message;
			if (isLost)
				return null;
			if (ReceivedPacket.hasError) {
				System.out.println("message has error setting flag now");
				messageToSend = setErrorFlag(message);
			}
			if (hasDelay) {
				try {
					System.out.println("Packet has been delayed");
					Thread.sleep(5000); // sleeps for 5 seconds
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				try {
					System.out.println("Packet has not been delayed");
					Thread.sleep(1000); // normal delay of 1 second
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			
			byte[] buffer = messageToSend.getBytes();
			InetAddress hostInet = null;
			try {
				hostInet = InetAddress.getByName(hostAddress);
			} catch (UnknownHostException uhex) {
				System.err.println("invalid host address");
				return null;
			}

			return new DatagramPacket(buffer, buffer.length, hostInet, port);
		}
		
		private String setErrorFlag(String message) {
			String tempMessage = message;
			char[] messageCharArray = tempMessage.toCharArray();
			messageCharArray[44] = '1';
			tempMessage = String.valueOf(messageCharArray);
			if (fromSender) {
				receivedPacket = createPacketToForward(message, destIP, destPort);
			} else {
				receivedPacket = createPacketToForward(message, sourceIP, sourcePort);
			}
			System.out.println("This packet contains an error, setting error flag now.");
			return tempMessage;
		}

		/**
		 * This method sends a packet to the designated address and port. It checks the
		 * command line arguments to determine if a packet should be dropped, delayed,
		 * or be marked as containing an error.
		 * 
		 * @param receivedPacket
		 * @param lostTest
		 * @param delayedTest
		 * @param errorTest
		 */
		private void forwardPacket(DatagramPacket receivedPacket) {
			try {
				networkSocket.send(receivedPacket);
			} catch (IOException e) {
				System.out.println("Network unable to forward packet");
				e.printStackTrace();
			}
		}

	}
}