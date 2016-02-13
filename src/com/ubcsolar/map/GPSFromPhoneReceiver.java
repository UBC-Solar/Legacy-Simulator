package com.ubcsolar.map;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Set;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.event.SentenceListener;
import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.util.Position;
import gnu.io.*;

import com.ubcsolar.common.LocationReport;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.sim.Log;

public class GPSFromPhoneReceiver implements Runnable, SentenceListener{
	private final MapController parent;
	private final String carName;
	private final String source; 
	
	private NRSerialPort serialPort;
	private SentenceReader NMEASentenceReader;
	
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
			serialPort = new NRSerialPort("COM7", 9600);
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
		parent.recordNewCarLocation(new LocationReport(location, this.carName, this.source, System.currentTimeMillis()));
	}


	@Override
	public void run() throws NullPointerException{
		InputStream is = serialPort.getInputStream();
		NMEASentenceReader = new SentenceReader(is);
		NMEASentenceReader.addSentenceListener(this, SentenceId.GGA);
		NMEASentenceReader.start();
	}
		
	public void stop(){
		NMEASentenceReader.stop();
		NMEASentenceReader.removeSentenceListener(this);
		serialPort.disconnect();
	}


	public void readingPaused() {
		System.out.println("-- Paused --");
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.event.SentenceListener#readingStarted()
	 */
	public void readingStarted() {
		System.out.println("-- Started --");
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.event.SentenceListener#readingStopped()
	 */
	public void readingStopped() {
		System.out.println("-- Stopped --");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * net.sf.marineapi.nmea.event.SentenceListener#sentenceRead(net.sf.marineapi
	 * .nmea.event.SentenceEvent)
	 */
	public void sentenceRead(SentenceEvent event) {
		// here we receive each sentence read from the port
		GGASentence gga = (GGASentence) event.getSentence();
		Position pos = gga.getPosition();
		
		GeoCoord coordinates = new GeoCoord(pos.getLatitude(), pos.getLongitude(), pos.getAltitude());
		if(parent != null){
			parent.recordNewCarLocation(new LocationReport(coordinates, this.carName, this.source, System.currentTimeMillis()));
		}else{
			System.out.println(coordinates);
		}
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
