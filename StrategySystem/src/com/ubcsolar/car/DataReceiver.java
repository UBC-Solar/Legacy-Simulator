/**
 * this class forms the receiver for the data transmission from the car. 
 * */

package com.ubcsolar.car;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TooManyListenersException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gnu.io.CommPortIdentifier; 
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
import gnu.io.UnsupportedCommOperationException;

//TODO turn this class into an abstract one, and move the listening implementation into a concrete
//subclass
public class DataReceiver implements Runnable,SerialPortEventListener { //needs to be threaded so it can listen for a response

	protected CarController myCarController; //the parent to notify of a new result. 
	private String name = "live"; //"live" because it's listening for real transmissions

	// values from the last received data are cached in here...
	public DataReceived data;

	private InputStream inputStream;
	private SerialPort serialPort;
	private Thread readThread;
	private byte[] serialReadBuf = new byte[500];
	private int serialReadBufPos = 0;
	private DataProcessor myDataProcessor;
	/**
	 * default constructor.
	 * @param toAdd - the CarController to notify when it gets a new result
	 */ 
	 
	public DataReceiver(CarController toAdd, DataProcessor theProcessor){
		myCarController = toAdd;
		myDataProcessor = theProcessor;
		// for now it always takes the first serial port.
		/*Enumeration portList = CommPortIdentifier.getPortIdentifiers();
		while(portList.hasMoreElements()){
			CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL){
				try{
					serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);
				}catch(PortInUseException e) {System.out.println(e);}
				try {
					inputStream = serialPort.getInputStream();
				} catch (IOException e) {System.out.println(e);}
				try {
					serialPort.addEventListener(this);
				} catch (TooManyListenersException e) {System.out.println(e);}
				serialPort.notifyOnDataAvailable(true);
				try {
					serialPort.setSerialPortParams(115200,
						SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);
				} catch (UnsupportedCommOperationException e) {System.out.println(e);}
				readThread = new Thread(this);
				readThread.start();
			}
		}*/
	}
	
	public static void main(String[] argv){
		DataReceiver dr = new DataReceiver(null, null);
	}
	
	public void loadJSONData(String jsonData){
		JSONObject data;
		DataReceived newData = new DataReceived();
		// test data
		//jsonData = "{\"speed\":100,\"totalVoltage\":44.4,\"stateOfCharge\":101,\"temperatures\":{\"bms\":40,\"motor\":50,\"pack0\":35,\"pack1\":36,\"pack2\":37,\"pack3\":38},\"cellVoltages\":{\"pack0\":[0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0,1.1,1.2],\"pack1\":[1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9,2.0,2.1,2.2],\"pack2\":[2.1,2.2,2.3,2.4,2.5,2.6,2.7,2.8,2.9,3.0,3.1,3.2],\"pack3\":[3.1,3.2,3.3,3.4,3.5,3.6,3.7,3.8,3.9,4.0,4.1,4.2]}}\n";
		try{
			data = new JSONObject(jsonData);			
		}catch(JSONException e){
			return; //malformed (corrupted) data is ignored.
		}
		newData.speed = (int) data.get("speed");
		newData.totalVoltage = (float) (double) data.get("totalVoltage");
		JSONObject temperatures = ((JSONObject) data.get("temperatures"));
		for(String key : JSONObject.getNames(temperatures))
			newData.temperatures.put(key, (int) temperatures.get(key));
		JSONObject cellVoltages = ((JSONObject) data.get("cellVoltages"));
		for(String key : JSONObject.getNames(cellVoltages)){
			int packID = key.toCharArray()[key.length()-1] - '0';
			newData.cellVoltages.put(packID, new ArrayList<Float>());
			JSONArray array = (JSONArray) cellVoltages.get(key);
			for(int i=0; i<array.length(); i++)
				newData.cellVoltages.get(packID).add((float) array.getDouble(i));
		}
		
		this.data = newData;	
	}
	 	
	/**
	 * DEPRECATED. Read the "data" variable instead.
	 * gets the last reported speed, in km/h. 
	 * @return the last repoted speed, in km/h. 
	 */
	@Deprecated
	public int getLastReportedSpeed(){
		return 99999; // garbage value. 
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {System.out.println(e);}
	}
		
	public void stop(){
		serialPort.notifyOnDataAvailable(false);
	}

	@Deprecated
	protected void checkForUpdate(){
		// test
		loadJSONData("{\"speed\":100,\"totalVoltage\":44.4,\"stateOfCharge\":101,\"temperatures\":{\"bms\":40,\"motor\":50,\"pack0\":35,\"pack1\":36,\"pack2\":37,\"pack3\":38},\"cellVoltages\":{\"pack0\":[0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0,1.1,1.2],\"pack1\":[1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9,2.0,2.1,2.2],\"pack2\":[2.1,2.2,2.3,2.4,2.5,2.6,2.7,2.8,2.9,3.0,3.1,3.2],\"pack3\":[3.1,3.2,3.3,3.4,3.5,3.6,3.7,3.8,3.9,4.0,4.1,4.2]}}\n");
	}

	/**
	 * 
	 * @return the name of the car loaded. 
	 */
	public String getName() {
		return name;
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		switch(event.getEventType()) {
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		case SerialPortEvent.DATA_AVAILABLE:

			try {
				while (inputStream.available() > 0) {
					serialReadBuf[serialReadBufPos] = (byte) inputStream.read();
					if(serialReadBuf[serialReadBufPos] == '\n'){
						serialReadBuf[serialReadBufPos+1] = 0;
						loadJSONData(new String(serialReadBuf));
						serialReadBufPos = 0;

						System.out.println(this.data.toString());
					}else{
						serialReadBufPos++;
					}
				}
			} catch (IOException e) {System.out.println(e);}
			break;
		}
	}

}
