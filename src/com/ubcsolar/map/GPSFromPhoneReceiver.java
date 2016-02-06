package com.ubcsolar.map;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TooManyListenersException;

import gnu.io.*;

import com.ubcsolar.common.CarLocation;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.sim.Log;

public class GPSFromPhoneReceiver implements Runnable{
	private final MapController parent;
	private final String carName;
	private final String source; 
	
	private NRSerialPort serialPort;
	
	/**
	 * 
	 * @param parent - the object to notify when a GPS event comes in
	 * @param carName - the name of the car that this even represents (in case we ever do multiple cars at once)
	 * @param source - where did it come from? GPS, Phone, manual entry via the UI?
	 */
	public GPSFromPhoneReceiver(MapController parent, String carName, String source){
		this.parent = parent;
		this.carName = carName;
		this.source = source;
		
		try{
			Set<String> portNames = NRSerialPort.getAvailableSerialPorts();
			String portName = portNames.iterator().next(); //it always gets the first serial port available. 
			System.out.println(portName);
			serialPort = new NRSerialPort(portName, 9600);
			serialPort.connect();
		} catch(NoSuchElementException e) {
			System.out.println("No serial ports");
			Log.write("ERROR: No Serial Ports");
			e.printStackTrace();
		}
	}
	
	
	public void GPSEventJustCameIn(String someInformation){
		// GPS coordinate randomly picked (in the mid-east states)
		//CarLocation(GeoCoord location, String carName, String source, double timeCreated)
		double lat = 39.9277;
		double lon = -83.684;
		double elevation = 327.203;
		GeoCoord location = new GeoCoord(lat,lon,elevation);
		parent.recordNewCarLocation(new CarLocation(location, this.carName, this.source, System.currentTimeMillis()));
	}


	@Override
	public void run() {
		InputStream is = serialPort.getInputStream();
		while(true){
			try {
				if(is.available() > 0){
					System.out.print((char)is.read());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
		
	public void stop(){
		serialPort.disconnect();
	}
	
	/**
	 * For testing the serial code on its own 
	 */
	public static void main(String[] args){
		GPSFromPhoneReceiver gpsrx = new GPSFromPhoneReceiver(null, null, null);
		gpsrx.run();
		return;
	}
}
