package com.ds.participant;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class MCCommandDispatcher implements Runnable {


	public final String SUCCESS = "successful";
	public final String FAIL = "failure";

	int participantID;
	String connectionDetails;
	int listenerPortNo;
	DataInputStream dispatcherInpStream;
	DataOutputStream dispatcherOutStream;
	String logFileName;
	ParticipantUtil utility ;
	MCReceiver receiverThread;
	
	public MCCommandDispatcher(int participantID, String connectionDetails,String logFileName) {
		// TODO Auto-generated constructor stub
		this.participantID = participantID;
		this.connectionDetails = connectionDetails;
		this.logFileName = logFileName;
	}

	public void run() {
		// TODO Auto-generated method stub

		try{
			String connectionParam [] = connectionDetails.split(":");
			
			Socket participantSock = new Socket(connectionParam[0],Integer.parseInt(connectionParam[1]));
			
			dispatcherInpStream = new DataInputStream(participantSock.getInputStream());
			dispatcherOutStream = new DataOutputStream(participantSock.getOutputStream());

			// read acknowledgement
			System.out.println(readFromInpStream());
			
			//send initial details to coordinator
			writeToOutStream(String.valueOf(participantID));
			
			Scanner sc = new Scanner(System.in);
			String userCmd;
			
			boolean executeParticipantStatus = true;
			utility = new ParticipantUtil(dispatcherInpStream,dispatcherOutStream);		
			while(executeParticipantStatus){
				Thread.sleep(1500);
				System.out.print("Participant " + participantID +" >> ");
				userCmd = sc.nextLine();
				executeParticipantStatus = executeCommandDispatch(userCmd);
			
			}
			
			return;// to close the thread
			
			
		}catch (Exception e){
			System.out.println("Exception in creating command dispatcher thread:: ");
			e.printStackTrace();
		}
		
	}
	
	private boolean executeCommandDispatch(String userCmd) {
		// TODO Auto-generated method stub
		
		ArrayList<String> command = parseString(userCmd);
		String status;
		try{

			switch (command.get(0)){
				
				case "register":
						
						utility.register(command.get(1));
						status = readFromInpStream();
						if(status.equals(SUCCESS)){//Registration successful
							//create multicast receiver thread
							makeMCReceiver(Integer.parseInt(command.get(1)), logFileName);
							Thread.sleep(1000);
							writeToOutStream("listener configured");
						}else{
							System.out.println("Participant is already register.");
							//return false;
						}
					break;
			
				case "deregister":
					utility.deregister();
					status = readFromInpStream();
					if(status.equals(SUCCESS)){//Deregistration successful
						receiverThread.disconnectCurrentReceiver();
						return false; // close participant after deregistration
					}else{
						System.out.println("Deregistration not completed. Participant needs to register first.");
					}
				
				case "disconnect":
					if(Participant.participantConnectionStatus){

						utility.disconnect();//ask server to disconnect the connection on listener port
						receiverThread.disconnectCurrentReceiver();//close the current listener port opened during registration
						Thread.sleep(500);
						status = readFromInpStream();
						if(status.equals(FAIL)){//"Disconnection unsuccessful"
							System.out.println("Could not disconnect.");
						}				
						
					}else{
						System.out.println("Participant is already disconnected.");
					}
					break;
				
				case "reconnect":
					//create MCReceiver Thread on new port number
					if(!Participant.participantConnectionStatus){
						makeMCReceiver(Integer.parseInt(command.get(1)),logFileName);
						Thread.sleep(500);
						utility.reconnect(command.get(1));// ask server to reconnect on the new port
						status = readFromInpStream();
						if(status.equals(FAIL)){//"Reconnect unsuccessful"
							System.out.println("Server :: Could not reconnect.");
						}
					}else{
						System.out.println("Participant is already connected.");
					}
					break;
				
				case "msend":
					if(Participant.participantConnectionStatus){
						utility.multicastSend(command.get(1));
						status = readFromInpStream();
						if(status.equals(FAIL)){//"msend unsuccessful"
							System.out.println("Msend unsuccessful");
						}
					}else{
						System.out.println("Participant needs to be connected. Msend failed");
					}
					break;
				default:
					System.out.println("Incorrect user command. Please enter proper command");
				}
			
		}catch(Exception e){
			System.out.println("Exception in execute command dispatch:: " + e.getMessage());
			e.printStackTrace();
		}
		
		return true;
		
	}

	private void makeMCReceiver(int listenerPortno,String logFileName) {
		// TODO Auto-generated method stub
		try{
			receiverThread = new MCReceiver(listenerPortno,logFileName);
			new Thread(receiverThread).start();
		}catch(Exception e){
			System.out.println("Exception in creating MC read thread");
			e.printStackTrace();
		}
	}

	private ArrayList<String> parseString(String userCmd) {
		// TODO Auto-generated method stub
		ArrayList<String> command = new ArrayList<>();
		try{
			
			command.add((userCmd.split(" ", 2)[0]).trim());
			if(userCmd.split(" ", 2).length > 1)
				command.add((userCmd.split(" ",2)[1]).trim());
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return command;
	}

	private void writeToOutStream(String msg) {
		// TODO Auto-generated method stub
		try{
			dispatcherOutStream.writeUTF(msg);
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private String readFromInpStream() {
		// TODO Auto-generated method stub
		String retMsg=null;
		try{
			retMsg = dispatcherInpStream.readUTF();
		}catch(IOException e){
			e.printStackTrace();
		}
		return retMsg;
	}

}
