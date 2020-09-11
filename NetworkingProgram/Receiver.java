
/**
 * Receiver - Project 2 - RDT 3.0 - Jacob Leiner COP 5518 Summer 2020
 * 
 * This program receives a packet, checks the seq number and error flag, and sets an ack flag within the packets message if there aren't any issues.
 * The packet is then sent back to the sender through a network program.  This program uses RDT 3.0 to ensure the integrity of the transmitted data.
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Receiver {

	private static final int BUFFER_SIZE = 256;
	private DatagramSocket _socket; // the socket for communication with clients
	private int _port; // the port number for communication with this server
	private boolean _continueService;
	private static StringBuilder messageReceived = new StringBuilder();
	private static char currentSeqNum = '1';

	/**
	 * Constructs a UDPserver object.
	 */
	public Receiver(int port) {
		_port = port;
	}

	/**
	 * Creates a datagram socket and binds it to a free port.
	 *
	 * @return - 0 or a negative number describing an error code if the connection
	 *         could not be established
	 */
	public int createSocket() {
		try {
			_socket = new DatagramSocket(_port);
		} catch (SocketException ex) {
			System.err.println("unable to create and bind socket");
			return -1;
		}

		return 0;
	}

	/**
     * this program executes a loop that receives packets and then sends a response
     */
	public void run()
	{
		// run server until gracefully shut down
		_continueService = true;
		
		while (_continueService) {
				
			System.out.println("currentSeqNum: " + currentSeqNum);
			DatagramPacket newDatagramPacket = receiveRequest();

			String request = new String (newDatagramPacket.getData()).trim();
				
			System.out.println("incoming request seq num: " + request.charAt(45));
			if (request.substring(47).equals("<exit/>")) {
				_continueService = false;
			}
				
				// check if the packet is corrupted or if sequence number is the same
				if (request.charAt(44) == '1') {
					System.out.println("Packet contained error flag, ignoring packet.");
				}
				if (request.charAt(45) == currentSeqNum) {
					System.out.println("Packet contained same sequence number of last acknowledged packet, ignoring packet.");
					continue;
				}
				
				// check if the packet's sequence number is the same as the previous
	
				if (request != null) {
	
					String response = setAck(request);
					System.out.println("Received Packet, now Sending Response...");
					System.out.println("Sending to Hostname: " + newDatagramPacket.getAddress().getHostName());
					System.out.println("Sending to Port: " + newDatagramPacket.getPort());
					System.out.println("response being sent: " + response);
	            
					sendResponse(
						response, 
						newDatagramPacket.getAddress().getHostName(), 
						newDatagramPacket.getPort());
				}
				
				if (!request.substring(47).equals("<exit/>")) {
					messageReceived.append(request.substring(47).trim());
					if (currentSeqNum == '0') {
						currentSeqNum = '1';
					} else {
						currentSeqNum = '0';
					}
				}

		}
	}

	/**
	 * This program sets the ack flag within the message being sent back to the
	 * sender program.
	 * 
	 * @param request
	 * @return a string of the existing packet with the ack flag set to 1
	 */
	private String setAck(String request) {
		String tempRequest = request;
		char[] responseCharArray = tempRequest.toCharArray();
		responseCharArray[46] = '1';
		tempRequest = String.copyValueOf(responseCharArray);
		System.out.println("request during setAck is: " + tempRequest);
		return tempRequest;
	}

	/**
	 * Creates a datagram from the specified request and destination host and port
	 * information.
	 *
	 * @param request  - the request to be submitted to the server
	 * @param hostname - the hostname of the host receiving this datagram
	 * @param port     - the port number of the host receiving this datagram
	 *
	 * @return a complete datagram or null if an error occurred creating the
	 *         datagram
	 */
	private DatagramPacket createDatagramPacket(String request, String hostname, int port) {
		byte buffer[] = new byte[BUFFER_SIZE];

		// empty message into buffer
		for (int i = 0; i < BUFFER_SIZE; i++) {
			buffer[i] = '\0';
		}

		// copy message into buffer
		byte data[] = request.getBytes();
		System.arraycopy(data, 0, buffer, 0, Math.min(data.length, buffer.length));

		InetAddress hostAddr;
		try {
			hostAddr = InetAddress.getByName(hostname);
		} catch (UnknownHostException ex) {
			System.err.println("invalid host address");
			return null;
		}

		return new DatagramPacket(buffer, BUFFER_SIZE, hostAddr, port);
	}

	/**
	 * Sends a request for service to the server. Do not wait for a reply in this
	 * function. This will be an asynchronous call to the server.
	 *
	 * @param response - the response to be sent
	 * @param hostAddr - the ip or hostname of the server
	 * @param port     - the port number of the server
	 *
	 * @return - 0, if no error; otherwise, a negative number indicating the error
	 */
	public int sendResponse(String response, String hostAddr, int port) {
		DatagramPacket newDatagramPacket = createDatagramPacket(response, hostAddr, port);
		if (newDatagramPacket != null) {
			try {
				_socket.send(newDatagramPacket);
			} catch (IOException ex) {
				System.err.println("unable to send message to server");
				return -1;
			}

			return 0;
		}

		System.err.println("unable to create message");
		return -1;
	}

	/**
	 * Receives a client's request.
	 *
	 * @return - the datagram containing the client's request or NULL if an error
	 *         occured
	 */
	public DatagramPacket receiveRequest() {
		byte[] buffer = new byte[BUFFER_SIZE];
		DatagramPacket newDatagramPacket = new DatagramPacket(buffer, BUFFER_SIZE);
		try {
			_socket.receive(newDatagramPacket);
		} catch (IOException ex) {
			System.err.println("unable to receive message from server");
			return null;
		}

		return newDatagramPacket;
	}


	/*
	 * Closes an open socket.
	 *
	 * @return - 0, if no error; otherwise, a negative number indicating the error
	 */
	public int closeSocket() {
		_socket.close();

		return 0;
	}

	/**
	 * The main function. Use this function for testing your code. We will provide a
	 * new main function on the day of the lab demo.
	 */
	public static void main(String[] args) {
		Receiver receiver;
		int receiverPort;

		if (args.length != 1) {
			System.err.println("Usage: Receiver <port number>\n");
			return;
		}

		try {
			receiverPort = Integer.parseInt(args[0]);
		} catch (NumberFormatException xcp) {
			System.err.println("Usage: Receiver <port number>\n");
			return;
		}

		// construct client and client socket
		receiver = new Receiver(receiverPort);
		if (receiver.createSocket() < 0) {
			return;
		}

		System.out.println("Receiver waiting for packets on port: " + receiverPort);

		receiver.run();
		System.out.println("Message received: " + messageReceived);

		receiver.closeSocket();
	}
}
