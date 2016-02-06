package com.ubcsolar.map;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

import com.ubcsolar.car.CarController;
import com.ubcsolar.car.DataProcessor;
import com.ubcsolar.car.XbeeSerialDataReceiver;
import com.ubcsolar.common.CarLocation;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.sim.Log;

public class GPSFromPhoneReceiver implements Runnable,SerialPortEventListener{
	private final MapController parent;
	private final String carName;
	private final String source; 
	
	private SerialPort serialPort;
	
	/**
	 * 
	 * @param parent - the object to notify when a GPS event comes in
	 * @param carName - the name of the car that this even represents (in case we ever do multiple cars at once)
	 * @param source - where did it come from? GPS, Phone, manual entry via the UI?
	 */
	public GPSFromPhoneReceiver(MapController parent, String carName, String source) throws SerialPortException{
		this.parent = parent;
		this.carName = carName;
		this.source = source;
		
		try{
			String[] portNames = SerialPortList.getPortNames();
			String portName = "NO SERIAL PORT";
			if(portNames.length > 0)
				portName = portNames[0]; //it always gets the first serial port available. 
			System.out.println(portName);
			serialPort = new SerialPort(portName);
			serialPort.openPort();
			serialPort.setParams(9600, 8, 1, 0); //where did these numbers come from?
			serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
		} catch(ArrayIndexOutOfBoundsException e) {
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
	public void serialEvent(SerialPortEvent event) {
		// we set event mask to SerialPort.MASK_RXCHAR so we don't check event type
		try {
			int bytesAvailable = event.getEventValue();
			while (bytesAvailable-->0) {
				System.out.print((char)serialPort.readBytes(1)[0]);
			}
		} catch (SerialPortException e) {System.out.println(e);}
	}


	@Override
	public void run() {
		try {
			serialPort.addEventListener(this);
		} catch (SerialPortException e) {
			System.out.println(e);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
		
	public void stop()throws SerialPortException{
		
			serialPort.removeEventListener();
			serialPort.closePort();
		
	}
	
	/**
	 * For testing the serial code on its own 
	 */
	public static void main(String[] args) throws SerialPortException{
		GPSFromPhoneReceiver gpsrx = new GPSFromPhoneReceiver(null, null, null);
		gpsrx.run();
		return;
	}
}
