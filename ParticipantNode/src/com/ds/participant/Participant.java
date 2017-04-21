package com.ds.participant;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Participant {

	/**
	 * @param args
	 */
	public static boolean participantConnectionStatus = false;
	
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		
		String participantConfigFile = args[0];
		ArrayList <String> configDetails = getConfigDetails(participantConfigFile);
		//ArrayList <String> configDetails = getConfigDetails("PP3-participant-conf.txt");
		int participantID = Integer.parseInt(configDetails.get(0)); //unique participant id
		String logFileName = configDetails.get(1); // log file to write received multicast messages
		String connectionDetails = configDetails.get(2);//ip:port of coordinator
		
		System.out.println(":::: Participant is up :::::");

		// Create a thread to make connection with coordinator
		makeMCCommandDispatcher(participantID, connectionDetails, logFileName); 
		
		
	}


	private static void makeMCCommandDispatcher(int participantID,String connectionDetails,String logFileName) {
		// TODO Auto-generated method stub
		try{
			new Thread(new MCCommandDispatcher(participantID,connectionDetails,logFileName)).start();
			return;
		}catch(Exception e){
			System.out.println("Exception in creating MC operations thread"+e.getMessage());
			//e.printStackTrace();
		}
	}

	private static ArrayList <String> getConfigDetails(String configFile) {
		// TODO Auto-generated method stub
		ArrayList <String> configDetails = new ArrayList<String>();
		try{
			
			BufferedReader br = new BufferedReader(new FileReader(configFile));
			String s = null;
			int i = 0;
			while((s = br.readLine()) != null){
				configDetails.add(s.trim());
			}
			
			if(br!= null){
				br.close();
			}
		}catch(Exception e){
			System.out.println("Coordinator :: Exception in file handling :: " + e.getMessage());
			//e.printStackTrace();
		}
		return configDetails;
	}
}
