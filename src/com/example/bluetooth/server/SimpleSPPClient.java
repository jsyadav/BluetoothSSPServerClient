package com.example.bluetooth.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class SimpleSPPClient {

	private void startClient() throws IOException{
		 
		  
		//Create a UUID for SPP
		    UUID uuid = new UUID("1101", true);//00001101-0000-1000-8000-00805F9B34FB
		    //Create the servicve url
		    String connectionString = "btspp://localhost:" + uuid +";name=Sample SPP Server";

		    //open server url
		    StreamConnection streamConn = (StreamConnection)Connector.open( connectionString );

		    boolean t = true;
		    while(t){
			    
			    RemoteDevice dev = RemoteDevice.getRemoteDevice(streamConn);
			    System.out.println("Remote device address: "+dev.getBluetoothAddress());
			    //System.out.println("Remote device name: "+dev.getFriendlyName(true));
			
			    OutputStream outStream=streamConn.openOutputStream();
			    /*BufferedWriter bWriter=new BufferedWriter(new OutputStreamWriter(outStream));
			    bWriter.write("Response String from SPP Server\r\n");*/
			
			    PrintWriter pWriter=new PrintWriter(new OutputStreamWriter(outStream));
			    //send response to spp client
			    pWriter.write("Request String to SPP Server "+ System.currentTimeMillis() +"\r\n");
			    pWriter.flush();
			    
			    //read string from spp client
			    InputStream inStream=streamConn.openInputStream();
			    BufferedReader bReader=new BufferedReader(new InputStreamReader(inStream));
				// waiting for client     	
				String lineRead=bReader.readLine();
				System.out.println(lineRead);
			 
			    pWriter.close();
			    
			    try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		    streamConn.close();

		  
	}
		  public static void main(String[] args) throws IOException{
			  try {
				LocalDevice localDevice = LocalDevice.getLocalDevice();
				System.out.println("Address: "+localDevice.getBluetoothAddress());
			    System.out.println("Name: "+localDevice.getFriendlyName());
				SimpleSPPClient sampleSPPClient=new SimpleSPPClient();
			    sampleSPPClient.startClient();
			} catch (BluetoothStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }

}
