package com.ds.participant;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MCReceiver implements Runnable{

	int listenerPortNo;
	Socket multicastSocket;
	DataInputStream ds;
	String logFileName;
	ServerSocket ss ;
	public MCReceiver(int listenerPortno, String logFileName) {
		// TODO Auto-generated constructor stub
		this.listenerPortNo = listenerPortno;
		this.logFileName = logFileName;
	}

	public void run() {
		// TODO Auto-generated method stub
		try{
			// accept connection from coordinator
			ss = new ServerSocket(listenerPortNo);
			//ss.setReuseAddress(true);
			multicastSocket = ss.accept();
			//multicastSocket.setReuseAddress(true);
			ds = new DataInputStream(multicastSocket.getInputStream());
			System.out.println("Listener created on port number :: "+listenerPortNo);
			while(true){
				if(ds != null){
					
					String msg = ds.readUTF();
					System.out.println("Multicast message :: " + msg);
					writeToLogs(msg);

				}
			}
			
		}catch(Exception e){
			System.out.println("Port Disconnected ::" + e.getMessage());
			//e.printStackTrace();
		}finally{
			disconnectCurrentReceiver();
		}
	}
	
	public void disconnectCurrentReceiver(){
		try{
			if(ds != null)
				ds.close();
			if(multicastSocket != null)
				multicastSocket.close();
			if(ss !=null)
				ss.close();
			
		}catch(IOException ioe){
			System.out.println("Exception in disconnect receiver" +ioe.getMessage());
			//ioe.printStackTrace();
		}
	}

	private void writeToLogs(String msg) {
		// TODO Auto-generated method stub
		try {
			PrintWriter pw = null;
			File file = new File(logFileName);
			FileWriter fw = null;
			if (!file.exists()) {
				file.createNewFile();
			}

			fw = new FileWriter(file.getAbsoluteFile(), true);
			pw = new PrintWriter(fw);
			pw.println(msg);
	
			pw.close();
			fw.close();
		}
		catch(Exception e) {
			System.out.println("Error in writing to log file"+e.getMessage());
		}
	}

}
