package com.ds.coordinator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class CoordinatorUtil {
	
	DataInputStream inpStream;
	DataOutputStream outStream;
	DataOutputStream multicastOutStream;
	ParticipantBean participant;

	public CoordinatorUtil(ParticipantBean participant,
			DataInputStream inpStream, DataOutputStream outStream) {
		// TODO Auto-generated constructor stub
		this.participant = participant;
		this.inpStream = inpStream;
		this.outStream = outStream;
		
	}

	public boolean register() {
		// TODO Auto-generated method stub
		try{
			//add to registered participant pool
			participant.connectionStatus = "Registered";
			if(Coordinator.participantStock.contains(participant))//if participant with same id is already registered
				return false;
			
			Coordinator.participantStock.add(participant);
			System.out.println("Registered participants in the system :: \n" + Coordinator.participantStock);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;	
	}

	public void deregister() {
		// TODO Auto-generated method stub
		try{
			//remove participant from participants pool
			System.out.println("Participant " + participant + " has deregistered");
			Coordinator.participantStock.remove(participant);
			System.out.println("Registered participants in the system :: \n" + Coordinator.participantStock);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void reconnect() {
		// TODO Auto-generated method stub
		try{
			int reconnectListenerPortNo = Integer.parseInt(inpStream.readUTF());// read new port number from user
			
			try{
				if(participant.makeListenerPortConnection(reconnectListenerPortNo));
			}catch (Exception e){
				System.out.println("Port Disconnected :: "+e.getMessage());
				e.printStackTrace();
			}
			participant.connectionStatus ="Registered";
			Thread.sleep(300);
			participant.flushBufferMsgs();//flush all the buffer messages having td < 120 secs
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void disconnect() {
		// TODO Auto-generated method stub
		try{
			participant.connectionStatus = "Disconnected";
			//disconnect the exisiting port connection
			participant.terminateListenerPortConnection();
			System.out.println(":: Participant "+participant+" is disconnected :: \n");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public synchronized void multicastSend() {
		// TODO Auto-generated method stub
		try{
			//read the message
			String message = inpStream.readUTF();
			
			for(ParticipantBean p : Coordinator.participantStock){
				if(p.connectionStatus.equals("Registered") && p.messageBuf.isEmpty()){
					p.getMulticastOutStream().writeUTF(message);
				}
				else if(p.connectionStatus.equals("Registered") && !p.messageBuf.isEmpty()){
					p.flushBufferMsgs();// empty all the messages in buffer having timestamp < td secs
					p.getMulticastOutStream().writeUTF(message);
				}
				else{
					//message added in LinkedList with a timestamp
					MessageBean msg = new MessageBean(message,System.currentTimeMillis());
					p.messageBuf.add(msg);
				}
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/*public void makeListenerPortConnection() {
		// TODO Auto-generated method stub
		try{
			Socket s = new Socket(participant.ipAddress,participant.portNo);
			participant.setMulticastOutStream(new DataOutputStream(s.getOutputStream()));
		}catch(Exception e){
			System.out.println("Exception in making listener port connection ::");
			e.printStackTrace();
		}
	}*/

}
