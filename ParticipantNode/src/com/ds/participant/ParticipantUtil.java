package com.ds.participant;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class ParticipantUtil {
	DataInputStream dispatcherInpStream;
	DataOutputStream dispatcherOutStream;
	
	public ParticipantUtil(DataInputStream dispatcherInpStream,DataOutputStream dispatcherOutStream) {
		// TODO Auto-generated constructor stub
		this.dispatcherInpStream = dispatcherInpStream;
		this.dispatcherOutStream = dispatcherOutStream;
	}

	public void register(String portNo) {
		// TODO Auto-generated method stub
		writeToOutStream("register");
		writeToOutStream(portNo);
		Participant.participantConnectionStatus = true;
	}

	public void deregister() {
		// TODO Auto-generated method stub
		System.out.println("Participant has deregistered. Signing off...");
		writeToOutStream("deregister");
		Participant.participantConnectionStatus = false;
	}

	public void disconnect() {
		// TODO Auto-generated method stub
		
		writeToOutStream("disconnect");
		Participant.participantConnectionStatus = false;
	}

	public void reconnect(String newPortNo) {
		// TODO Auto-generated method stub
		writeToOutStream("reconnect");
		writeToOutStream(newPortNo);
		Participant.participantConnectionStatus = true;
	}

	public void multicastSend(String multicastMessage) {
		// TODO Auto-generated method stub
		try{
			//send msg on output line
			writeToOutStream("msend");
			writeToOutStream(multicastMessage);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
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
