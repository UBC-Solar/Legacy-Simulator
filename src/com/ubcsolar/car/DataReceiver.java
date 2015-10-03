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

import jssc.*;

//TODO turn this class into an abstract one, and move the listening implementation into a concrete
//subclass
public class DataReceiver implements Runnable,SerialPortEventListener{ //needs to be threaded so it can listen for a response

	protected CarController myCarController; //the parent to notify of a new result. 
	private String name = "live"; //"live" because it's listening for real transmissions

	// values from the last received data are cached in here...
	public DataReceived data = new DataReceived();

	private SerialPort serialPort;
	private byte[] serialReadBuf = new byte[500];
	private int serialReadBufPos = 0;
	private DataProcessor myDataProcessor;
	/**
	 * default constructor.
	 * @param toAdd - the CarController to notify when it gets a new result
	 */ 
	 
	public DataReceiver(CarController toAdd, DataProcessor theProcessor) throws SerialPortException{
		myCarController = toAdd;
		myDataProcessor = theProcessor;
		
		try{
			String[] portNames = SerialPortList.getPortNames();
			String portName = "NO SERIAL PORT";
			if(portNames.length > 0)
				portName = portNames[0];
			System.out.println(portName);
			serialPort = new SerialPort(portName);
			serialPort.openPort();
			serialPort.setParams(115200, 8, 1, 0);
			serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
		} catch(ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] argv) throws SerialPortException{
		DataReceiver dr = new DataReceiver(null, null);
	}
	
	public void loadJSONData(String jsonString){
		JSONObject jsonData;
		DataReceived newData = new DataReceived();
		// test data
		//jsonData = "{\"speed\":100,\"totalVoltage\":44.4,\"stateOfCharge\":101,\"temperatures\":{\"bms\":40,\"motor\":50,\"pack0\":35,\"pack1\":36,\"pack2\":37,\"pack3\":38},\"cellVoltages\":{\"pack0\":[0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0,1.1,1.2],\"pack1\":[1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9,2.0,2.1,2.2],\"pack2\":[2.1,2.2,2.3,2.4,2.5,2.6,2.7,2.8,2.9,3.0,3.1,3.2],\"pack3\":[3.1,3.2,3.3,3.4,3.5,3.6,3.7,3.8,3.9,4.0,4.1,4.2]}}\n";
		try{
			jsonData = new JSONObject(jsonString);
		}catch(JSONException e){
			return; //malformed (corrupted) data is ignored.
		}
		newData.speed = (int) jsonData.get("speed");
		newData.totalVoltage = (int) jsonData.get("totalVoltage");
		JSONObject temperatures = ((JSONObject) jsonData.get("temperatures"));
		for(String key : JSONObject.getNames(temperatures))
			newData.temperatures.put(key, (int) temperatures.get(key));
		JSONObject cellVoltages = ((JSONObject) jsonData.get("cellVoltages"));
		for(String key : JSONObject.getNames(cellVoltages)){
			int packID = key.toCharArray()[key.length()-1] - '0';
			newData.cellVoltages.put(packID, new ArrayList<Float>());
			JSONArray array = (JSONArray) cellVoltages.get(key);
			for(int i=0; i<array.length(); i++)
				newData.cellVoltages.get(packID).add((float) array.getDouble(i));
		}
		
		this.data = newData;
		this.myDataProcessor.store(newData);
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
			serialPort.addEventListener(this);
		} catch (SerialPortException e) {
			System.out.println(e);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
		
	public void stop(){
		try {
			serialPort.removeEventListener();
		} catch (SerialPortException e) {e.printStackTrace();}
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
		// we set event mask to SerialPort.MASK_RXCHAR so we don't check event type
		try {
			int bytesAvailable = event.getEventValue();
			while (bytesAvailable-->0) {
				serialReadBuf[serialReadBufPos] = (byte) serialPort.readBytes(1)[0];
				if(serialReadBuf[serialReadBufPos] == '\n'){
					serialReadBuf[serialReadBufPos+1] = 0;
					loadJSONData(new String(serialReadBuf));
					serialReadBufPos = 0;

					System.out.println(this.data.toString());
				}else{
					serialReadBufPos++;
				}
			}
		} catch (SerialPortException e) {System.out.println(e);}
	}

}
