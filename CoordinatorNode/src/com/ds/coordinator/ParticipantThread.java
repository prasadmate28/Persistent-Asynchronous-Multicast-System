package com.ds.coordinator;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;

public class ParticipantThread implements Runnable {

	public final String SUCCESS = "successful";
	public final String FAIL = "failure";
	public final String REGISTERED = "Registered";
	public final String DISCONNECTED = "Disconnected";

	DataInputStream inpStream;
	DataOutputStream outStream;
	InetAddress participantAddr;
	CoordinatorUtil c_util;

	public ParticipantThread(Socket c_socket) throws IOException {
		// TODO Auto-generated constructor stub
		inpStream = new DataInputStream(c_socket.getInputStream()); // data i/o stream on server port
		outStream = new DataOutputStream(c_socket.getOutputStream()); // data i/o stream on server port
		participantAddr = c_socket.getInetAddress();

	}

	public void run() {
		// TODO Auto-generated method stub

		try {
			// send acknowledgement for participant connection on serverport
			writeToOutStream("Connection successful");

			// receive initial participant details
			String participantDetails = readFromInpStream();

			// Create a participant bean instance
			ParticipantBean participant = new ParticipantBean(
					Integer.parseInt(participantDetails), participantAddr, null);
			c_util = new CoordinatorUtil(participant, inpStream, outStream);

			System.out.println("Participant Thread created :: " + participant);

			while (true) {

				String userCmd = readFromInpStream();// /read user input

				if (!userCmd.equals("deregister")) {

					switch (userCmd) {

					case "register":
						if (participant.connectionStatus == null) {
							participant.setPortNo(Integer.parseInt(readFromInpStream()));// set listener port number
							if (c_util.register()) {// true if participant id is unique
								writeToOutStream(SUCCESS);// "Registration successful"
								String status = readFromInpStream();
								if (status.equals("listener configured")) {
									participant.makeListenerPortConnection(participant.getPortNo());
								}
							} else {
								writeToOutStream(FAIL);// "Registration unsuccessful"
								System.out.println("Participant with same ID already registered");
							}
						} else {
							writeToOutStream(FAIL);// "Registration unsuccessful"
							System.out.println("Participant Already registered");
						}
						break;

					case "reconnect":
						if (participant.connectionStatus != null && 
						participant.connectionStatus.equals(DISCONNECTED)) {//"Disconnected"
							c_util.reconnect();
							System.out.println("After reconnect status:: "	+ participant.connectionStatus);
							writeToOutStream(SUCCESS);//"Reconnected successfully"
						} else {
							writeToOutStream(FAIL);//"Reconnect unsuccessful"
						}
						break;
					case "disconnect":
						if (participant.connectionStatus != null && 
						participant.connectionStatus.equals(REGISTERED)) {//"Registered"
							c_util.disconnect();
							writeToOutStream(SUCCESS);//"Disconnected successfully"
						} else {
							writeToOutStream(FAIL);//"Disconnection unsuccessful"
						}
						break;
					case "msend":
						if (participant.connectionStatus != null && 
						participant.connectionStatus.equals(REGISTERED)) {//"Registered"
							c_util.multicastSend();
							writeToOutStream(SUCCESS);//"msend successful"
						} else {
							writeToOutStream(FAIL);//"msend unsuccessful"
						}
						break;
					default:
						System.out.println("Invalid command:: ");
					}// switch case ends

				} else {

					if (participant.connectionStatus != null) {
						c_util.deregister();
						writeToOutStream(SUCCESS);//"Deregistration successful"
						break;
					} else {
						writeToOutStream(FAIL);//"Deregistration unsuccessful"
					}

				}

			}

		} catch (IOException ef) {
			System.out.println(":: Participant terminated deliberately ::"
					+ ef.getMessage());
		} catch (Exception e) {
			System.out.println("Exception in executing commands :::" +e.getMessage());
			//e.printStackTrace();
		}
	}

	private void writeToOutStream(String msg) {
		// TODO Auto-generated method stub
		try {
			outStream.writeUTF(msg);
		} catch (IOException e) {
			System.out.println("Exception in wrting on output stream " + e.getMessage());
			//e.printStackTrace();
		}
	}

	private String readFromInpStream() {
		// TODO Auto-generated method stub
		String retMsg = null;
		try {
			retMsg = inpStream.readUTF();
		} catch (IOException e) {
			System.out.println("Exception in reading input stream " + e.getMessage());
			//e.printStackTrace();
		}
		return retMsg;
	}

}
