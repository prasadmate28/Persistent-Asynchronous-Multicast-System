package com.ds.coordinator;

import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
//import java.util.HashMap;
import java.util.LinkedList;

public class ParticipantBean {

	int id,portNo;
	InetAddress ipAddress;
	String connectionStatus;
	LinkedList<MessageBean> messageBuf;
	DataOutputStream multicastOutStream;
	Socket p_socket;
	//HashMap<Integer,Socket> portsUsed;

	public ParticipantBean(int id, InetAddress ipAddress, String connectionStatus) {
		this.id = id;
		this.ipAddress = ipAddress;
		this.connectionStatus = connectionStatus;
		this.messageBuf = new LinkedList<MessageBean>();
	}
	
	public int getPortNo() {
		return portNo;
	}
	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}



	public DataOutputStream getMulticastOutStream() {
		return multicastOutStream;
	}

	public void setMulticastOutStream(DataOutputStream multicastOutStream) {
		this.multicastOutStream = multicastOutStream;
	}
	
	public void terminateListenerPortConnection(){
		
		try{
			if(multicastOutStream != null){
				//multicastOutStream.close();
				//multicastOutStream = null;
				//portNo = 0;
			}
			if(p_socket != null){
				//p_socket.close();
				//portsUsed.add(portNo);
			}
			else
				System.out.println("Output stream connection is null");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void flushBufferMsgs(){
	try{
		
		while(!messageBuf.isEmpty()){
			MessageBean m = messageBuf.poll();
			if((System.currentTimeMillis() - m.timestamp)/1000 <= Coordinator.thresholdTime){
				multicastOutStream.writeUTF(m.message);
			}
			
		}

	}catch(Exception e){
		e.printStackTrace();
	}
		
	}
	
	
	public boolean makeListenerPortConnection(int new_port_no) throws Exception{
		// TODO Auto-generated method stub
			
		this.portNo = new_port_no;
		p_socket = new Socket(ipAddress,portNo);
		multicastOutStream = new DataOutputStream(p_socket.getOutputStream());
		return true;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParticipantBean other = (ParticipantBean) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[id= " + id + ", connectionStatus=" + connectionStatus+"]";
	}
	
}
