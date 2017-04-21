package com.ds.coordinator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashSet;

import com.sun.security.auth.login.ConfigFile;

import jdk.nashorn.internal.runtime.regexp.joni.Config;

public class Coordinator {

	/**
	 * @param args
	 * @throws IOException 
	 */
	static int thresholdTime,co_ordinator_port;
	static HashSet<ParticipantBean> participantStock = new HashSet<ParticipantBean>();// inventory of registered participant
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		//String configFile = args[0];
		//ArrayList <Integer> configDetails = getConfigDetails(configFile);
		ArrayList <Integer> configDetails = getConfigDetails("PP3-coordinator-conf.txt"); 
		
		co_ordinator_port = configDetails.get(0);
		thresholdTime = configDetails.get(1);
		ServerSocket c_socket = new ServerSocket(co_ordinator_port);
		
		System.out.println("Coordinator started :: Port no " + co_ordinator_port);
		
		while(true){
			//create a participantThread connection on coordinator
			new Thread(new ParticipantThread(c_socket.accept())).start();
		}
		
	}

	private static ArrayList <Integer> getConfigDetails(String configFile) {
		// TODO Auto-generated method stub
		ArrayList <Integer> configDetails = new ArrayList <Integer>();
		try{
			
			BufferedReader br = new BufferedReader(new FileReader(configFile));
			String s = null;
			int i = 0;
			while((s = br.readLine()) != null){
				configDetails.add(Integer.parseInt(s.trim()));
			}
			
			if(br!= null){
				br.close();
			}
		}catch(Exception e){
			System.out.println("Coordinator :: Exception in file handling :: " + e.getMessage());
			e.printStackTrace();
		}
		return configDetails;
	}

}
