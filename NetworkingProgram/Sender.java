/**
 * Sender - Project 2 - RDT 3.0 - Jacob Leiner COP 5518 Summer 2020
 * 
 * This program accepts several parameters from the command line to create a a simulated network protocol header that is combined with a chunked 
 * message that is obtained from user input.  The program waits for a response, and then moves on to the next part of the message.  This program implements
 * RDT 3.0 by using an error flag, a seq flag, and an ack flag to determine whether a packet needs to be resent or not.
 * 
 */


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Sender {

    private static final int BUFFER_SIZE = 256;
    private DatagramSocket _socket; // the socket for communication with a server

    /**
     * Constructs a Sender object.
     */
    public Sender() {
    }

    /**
     * Creates a datagram socket and binds it to a free port.
     *
     * @return - 0 or a negative number describing an error code if the connection could not be established
     */
    public int createSocket(int port) {
        try {
            _socket = new DatagramSocket(port);
        } catch (SocketException ex) {
            System.err.println("unable to create and bind socket");
            return -1;
        }

        return 0;
    }

    /**
     * Sends a request for service to the server. Do not wait for a reply in this function. This will be
     * an asynchronous call to the server.
     *
     * @param request - the request to be sent
     * @param networkHeader - header string created from command line args
     * @param hostAddr - the ip or hostname of the server
     * @param port - the port number of the server
     * @throws IOException, Socket 
     *
     */
    public void sendRequest(String request, String networkHeader, String hostAddr, int port){
    	// need to break up request paramater into 7 byte chunks and add to networkHeaderString
    	int currentIndex = 0;
    	int requestLength = request.length();
    	int seq = 0;
    	try {
			_socket.setSoTimeout(4000); // set timeout to 4 seconds
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
    	// loop to break up user input into chunks to send in packets
    	while (currentIndex < requestLength) {
	        DatagramPacket newDatagramPacket = createDatagramPacket(request, requestLength, currentIndex, seq, networkHeader, hostAddr, port);
	        System.out.println("current message being sent: " + new String(newDatagramPacket.getData()).substring(47));
	        if (newDatagramPacket != null) {
	            try {
	                _socket.send(newDatagramPacket);
	            } catch (IOException ex) {
	                System.err.println("unable to send message to receiver");
	            }
	        }

	        // receiveResponse from receiver, checking for ack.  if ack present and responseSeq is the seq sent, send next chunk
	        byte[] buffer = new byte[BUFFER_SIZE];
	        DatagramPacket responseDatagramPacket = new DatagramPacket(buffer, BUFFER_SIZE);
	        try {
				_socket.receive(responseDatagramPacket);
			} catch (SocketTimeoutException stex) {
				continue;
    		} catch (IOException e) {
				continue;
			}	        
	        
	        String response = new String(buffer).trim();
	        System.out.println("response received: " + response);
	        if (checkForAckAndSeq(seq, response)) {
	    		System.out.println("Received ack for previous message from Receiver!  Sending next message....");
	        	currentIndex += Math.min(7, requestLength - currentIndex);
	        	if (seq == 0) {
	        		seq = 1;
	        	} else {
	        		seq = 0;
	        	}
	        }
    	}
    	
    	// this packet lets the receiver know the last packet has been sent
    	DatagramPacket finalPacketToNotifyReceiver = createDatagramPacket("<exit/>", 10, 0, 0, networkHeader, hostAddr, port);
    	System.out.println("sending final packet to notify receiver that the last packet has been sent and acknowledged.");
    	try {
			_socket.send(finalPacketToNotifyReceiver);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("final packet did not send");
			e.printStackTrace();
		}
    }


    /**
     * checks for an ack
     * 
     * @param seq - the seq number of the packet that was just sent
     * @param response - the response received from the receiver
     * 
     * @return boolean for whether the required flags have been set to move on to the next chunk of message
     */
	private boolean checkForAckAndSeq(int seq, String response) {
		String seqChar = Integer.toString(seq);
		return response.charAt(46) == '1' && response.charAt(45) == seqChar.charAt(0);
	}

	/**
     * Creates a datagram from the specified request and destination host and port information.
     *
     * @param request - the request to be submitted to the server
     * @param hostname - the hostname of the host receiving this datagram
     * @param port - the port number of the host receiving this datagram
     *
     * @return a complete datagram or null if an error occurred creating the datagram
     */
    private DatagramPacket createDatagramPacket(String request, int reqLength, int index, int seq, String networkHeader, String hostname, int port)
    {
        byte buffer[] = new byte[BUFFER_SIZE];

        // empty message into buffer
        for (int i = 0; i < BUFFER_SIZE; i++) {
            buffer[i] = '\0';
        }
        
        // combine chunk of request with networkHeader
        String combinedMessage = networkHeader + "0" + seq + "0" + request.substring(index, Math.min(reqLength, index + 7));
        System.out.println("currentMessageSending: " + combinedMessage);

        // copy message into buffer
        byte data[] = combinedMessage.getBytes();
        System.arraycopy(data, 0, buffer, 0, Math.min(data.length, buffer.length));
        
        InetAddress hostAddr;
        try {
            hostAddr = InetAddress.getByName(hostname);
        } catch (UnknownHostException ex) {
            System.err.println ("invalid host address");
            return null;
        }

        return new DatagramPacket (buffer, BUFFER_SIZE, hostAddr, port);
    }

    /**
     * Receives the server's response following a previously sent request.
     *
     * @return - the server's response or NULL if an error occured
     */
    public String receiveResponse() {
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket newDatagramPacket = new DatagramPacket(buffer, BUFFER_SIZE);
        try {
			_socket.receive(newDatagramPacket);
		} catch (IOException e) {
			return null;
		}


        return new String(buffer).trim();

    }

    /*
     * Prints the response to the screen in a formatted way.
     *
     * response - the server's response as an XML formatted string
     *
     */
    public static void printResponse(String response) {
        System.out.println("FROM SERVER: " + response);
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
     * The main function. Use this function for
     * testing your code. We will provide a new main function on the day of the lab demo.
     */
    public static void main(String[] args)
    {
        Sender sender;
        int 	  senderPort;
        String    receiverName;
        int 	  receiverPort;
        String    networkName;
        int 	  networkPort;
        String    req;
        String networkHeader = "";

        if (args.length != 5) {
            System.err.println("Usage: Sender <senderPort> <receiverName> <receiverPort>\n");
            return;
        }
        try {
        	senderPort = Integer.parseInt(args[0]);
        } catch (NullPointerException xcp) {
            System.err.println("Usage: Sender <senderPort> <receiverName> <receiverPort> <networkName> <receiverName>\n");
            return;
        }

        try {
        	receiverName = args[1];
        } catch (NumberFormatException xcp) {
            System.err.println("Usage: Sender <senderPort> <receiverName> <receiverPort> <networkName> <receiverName>\n");
            return;
        }
        
        try {
        	receiverPort = Integer.parseInt(args[2]);
        } catch (NumberFormatException xcp) {
            System.err.println("Usage: Sender <senderPort> <receiverName> <receiverPort> <networkName> <receiverName>\n");
            return;
        }
        
        try {
        	networkName = args[3];
        } catch (NumberFormatException xcp) {
            System.err.println("Usage: Sender <senderPort> <receiverName> <receiverPort> <networkName> <receiverName>\n");
            return;
        }
        
        try {
        	networkPort = Integer.parseInt(args[4]);
        } catch (NumberFormatException xcp) {
            System.err.println("Usage: Sender <senderPort> <receiverName> <receiverPort> <networkName> <receiverName>\n");
            return;
        }

        // construct Sender and Sender socket
        sender = new Sender();
        if (sender.createSocket(senderPort) < 0) {
            return;
        }
        
        // creates network header string
        networkHeader = createHeader(senderPort, receiverPort, receiverName);

        // gather input from user to transmit
        System.out.print ("Enter a request: ");
        Scanner sc = new Scanner(System.in);
        req = sc.nextLine();
        System.out.println("num of characters in request: " + req.length());
        sc.close();


        // this breaks up the request into 7 byte chunks and sends to the receiver, waiting for an ack before
        // sending the next chunk of the request
        sender.sendRequest(req, networkHeader, networkName, networkPort);

        sender.closeSocket();
    }
    
    
    /**
     * method to create network header string from inputs to include in message sent to receiver
     * 
     * @param int senderport
     * @param int receiverPort
     * @param String receiverName
     * 
     * @return String made up of required fields
     */
	private static String createHeader(int senderPort, int receiverPort, String receiverName) {
		// create inetaddress for this (sender) program
		InetAddress senderInet = null;
		try {
			senderInet = InetAddress.getByName(null);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("failed to create sender InetAddress");
		}

		// create inetaddress for the receiver program
		InetAddress receiverInet = null;
		try {
			receiverInet = InetAddress.getByName(receiverName);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("failed to create receiver InetAddress");
		}

		// create string for simulated transport layer
		String networkHeader = "" + String.format("%-16s", senderInet.getHostAddress() + "\\").replace(' ', '0')
				+ String.format("%-6s", senderPort + "\\").replace(' ', '0')
				+ String.format("%-16s", receiverInet.getHostAddress() + "\\").replace(' ', '0')
				+ String.format("%-6s", receiverPort + "\\").replace(' ', '0');

		System.out.println("network header string: " + networkHeader);
		
		return networkHeader;
	}
}
