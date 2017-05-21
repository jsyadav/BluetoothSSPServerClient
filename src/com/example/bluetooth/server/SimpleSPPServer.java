package com.example.bluetooth.server;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import com.arogita.appclient.protocol.EncodeDecode;
import com.arogita.appclient.protocol.Request;
import com.arogita.appclient.protocol.RequestCommand;

/**
* Class that implements an SPP Server which accepts single line of
* message from an SPP client and sends a single line of response to the client.
*/
public class SimpleSPPServer {

//start server
private void startServer() throws IOException{

    //Create a UUID for SPP
    UUID uuid = new UUID("1101", true);//00001101-0000-1000-8000-00805F9B34FB
    //Create the servicve url
    String connectionString = "btspp://localhost:" + uuid +";name=Sample SPP Server";
    //String connectionString = "btspp://localhost:" + uuid +";authenticate=true;encrypt=false;master=false;name=Sample SPP Server";
    //btspp://localhost:" +"0000110100001000800000805F9B34FB;name=BtExample;authenticate=false;encrypt=false;master=false"
                                                                     

    //open server url
    StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier)Connector.open( connectionString );
    
    boolean t = true;
    while(t){

	  //Wait for client connection
	    System.out.println("\nServer Started. Waiting for clients to connect...");
	    StreamConnection connection=streamConnNotifier.acceptAndOpen();
	    System.out.println("New client connects...");
	    new WorkerThread(connection).run();
	    /*
	    RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
	    System.out.println("Remote device address: "+dev.getBluetoothAddress());
	    //System.out.println("Remote device name: "+dev.getFriendlyName(true));

	    //read string from spp client
	    InputStream inStream=connection.openInputStream();
	   

	    //read request 
	    byte[] b = new byte[6];
	    inStream.read(b, 0, 6);
	    System.out.println("Input Message: " +EncodeDecode.byteArrayToHexString(b));
	    byte[] command = new byte[2];
	    command[0] = b[3];command[1] = b[4];
	    String comm = RequestCommand.getCommandName(command);
	    
	    
	    //send response to spp client
	    String outMessage = "Response String from SPP Server "+ comm + ", time "+ System.currentTimeMillis() +"\r\n";
	    OutputStream outStream=connection.openOutputStream();
	    PrintWriter pWriter=new PrintWriter(new OutputStreamWriter(outStream));
	    pWriter.write(outMessage);
	    pWriter.flush();
	    System.out.println("Output Message: " +EncodeDecode.byteArrayToHexString(b));
    	//pWriter.close();
    	 
    	 */
	    
    }
    
    streamConnNotifier.close();

}


public class WorkerThread implements Runnable{

	private StreamConnection clientConnection;
	private InputStream inStream = null;
	private OutputStream outStream = null;
	private PrintWriter pWriter = null;
	
	public WorkerThread(StreamConnection connection){
		this.clientConnection = connection;
		RemoteDevice dev;
		try {
			dev = RemoteDevice.getRemoteDevice(clientConnection);
			System.out.println("Remote device address: "+dev.getBluetoothAddress());
		    //System.out.println("Remote device name: "+dev.getFriendlyName(true));
		    //read string from spp client
		    inStream=connection.openInputStream();
		    outStream=connection.openOutputStream();
		    pWriter=new PrintWriter(new OutputStreamWriter(outStream));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
	    
		while(true){
		    //read request 
		    byte[] b = new byte[6];
		    try {		    	
				int n = inStream.read(b, 0, 6);
				
				if (n <=0 ){
					if (n < 0){
						System.out.println("Client disconnected...");
						return;
					}
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}
				System.out.println("Input Message: " +EncodeDecode.byteArrayToHexString(b));
			    byte[] command = new byte[2];
			    command[0] = b[3];command[1] = b[4];
			    //String comm = RequestCommand.getCommandName(command);
			    
			    
			    //send response to spp client
			    //String outMessage = "Response String from SPP Server "+ comm + ", time "+ System.currentTimeMillis() +"\r\n";
		
			    //pWriter.write(outMessage);
			    //pWriter.flush();
			    byte[] outMesg = sendSpo2();
			    System.out.println("Output Message: " +EncodeDecode.byteArrayToHexString(outMesg));
			    outStream.write(outMesg);
			    outStream.flush();
			    
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		}
    	//pWriter.close();
    	 
	}
	
}

public byte[] sendSpo2(){
	//SPO2 Param 0x04 SPO2 Status Spo2Sat PulseRate
	EncodeDecode ed = new EncodeDecode();
	byte[] command = new byte[4];
	command[0] = 0x04;// SPO2
	command[1] = 0x00;// status 
	command[2] = 0x5F;// saturation 95
	command[3] = 0x6E;// pulse 110
	Request req = new Request(command);
	return ed.encode(req);
}


public static void main(String[] args) throws IOException {

    //display local device address and name
    LocalDevice localDevice = LocalDevice.getLocalDevice();
    System.out.println("Address: "+localDevice.getBluetoothAddress());
    System.out.println("Name: "+localDevice.getFriendlyName());

    SimpleSPPServer sampleSPPServer=new SimpleSPPServer();
    sampleSPPServer.startServer();

}
}
