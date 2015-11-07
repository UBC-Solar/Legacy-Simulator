/**
 * this class forms the receiver for the data transmission from the car. 
 * */

package com.ubcsolar.car;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ubcsolar.common.LogType;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.common.TelemDataPacket;
import com.ubcsolar.sim.Log;

import jssc.*;

public class XbeeSerialDataReceiver extends AbstractDataReceiver implements Runnable,SerialPortEventListener{ //needs to be threaded so it can listen for a response


	// values from the last received data are cached in here...
	public TelemDataPacket lastLoadedDataPacket;

	private SerialPort serialPort;
	private byte[] serialReadBuf = new byte[500];
	private int serialReadBufPos = 0;
	private DataProcessor myDataProcessor;
	
	@Override
	void setName() {
		this.name = "Real Car";
	}
	/**
	 * default constructor.
	 * @param toAdd - the CarController to notify when it gets a new result
	 */ 
	 //TODO change this so TelemDataPacket doesn't need public variables. 
	public XbeeSerialDataReceiver(CarController toAdd, DataProcessor theProcessor) throws SerialPortException{
		super(toAdd, theProcessor);
		
		try{
			String[] portNames = SerialPortList.getPortNames();
			String portName = "NO SERIAL PORT";
			if(portNames.length > 0)
				portName = portNames[0]; //it always gets the first serial port available. 
			System.out.println(portName);
			serialPort = new SerialPort(portName);
			serialPort.openPort();
			serialPort.setParams(115200, 8, 1, 0); //where did these numbers come from?
			serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("No serial ports");
			Log.write("ERROR: No Serial Ports");
			e.printStackTrace();
		}
	}
	
	/**
	 * This method turns the JsonString that we received into a TelemDataPacket for 
	 * transfer to the DataProcessor
	 * @param jsonString - the string received from the xbee serial port. 
	 */
	//NOAH: Can we move this method into the data processor? Probably not 
	//super big on processing, but it would help prevent a crash if this 
	//enters an infinite loop or encounters an exception it couldn't handle. 
	//(wouldn't need to rebuilt the entire serial port to recover, just the processor. 
	public void loadJSONData(String jsonString){
		JSONObject jsonData;
		
		// test data
		//jsonData = "{\"speed\":100,\"totalVoltage\":44.4,\"stateOfCharge\":101,\"temperatures\":{\"bms\":40,\"motor\":50,\"pack0\":35,\"pack1\":36,\"pack2\":37,\"pack3\":38},\"cellVoltages\":{\"pack0\":[0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0,1.1,1.2],\"pack1\":[1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9,2.0,2.1,2.2],\"pack2\":[2.1,2.2,2.3,2.4,2.5,2.6,2.7,2.8,2.9,3.0,3.1,3.2],\"pack3\":[3.1,3.2,3.3,3.4,3.5,3.6,3.7,3.8,3.9,4.0,4.1,4.2]}}\n";
		try{
			jsonData = new JSONObject(jsonString);
		}catch(JSONException e){
			SolarLog.write(LogType.ERROR, System.currentTimeMillis(), "Received a Corrupt Packet");
			System.out.println("Received a Corrupt Packet");
			return; //malformed (corrupted) data is ignored.
		}
		int speed = (int) jsonData.get("speed");
		int totalVoltage = (int) jsonData.get("totalVoltage");
		JSONObject temperatures = ((JSONObject) jsonData.get("temperatures"));
		HashMap<String,Integer> mapForTemperatures = new HashMap<String,Integer>();
		for(String key : JSONObject.getNames(temperatures))
			mapForTemperatures.put(key, (int) temperatures.get(key));
		JSONObject cellVoltages = ((JSONObject) jsonData.get("cellVoltages"));
		HashMap<Integer,ArrayList<Float>> mapForCellVoltages = new HashMap<Integer,ArrayList<Float>>();
		for(String key : JSONObject.getNames(cellVoltages)){
			int packID = key.toCharArray()[key.length()-1] - '0';
			mapForCellVoltages.put(packID, new ArrayList<Float>());
			JSONArray array = (JSONArray) cellVoltages.get(key);
			for(int i=0; i<array.length(); i++)
				mapForCellVoltages.get(packID).add((float) array.getDouble(i));
		}
		/*(int newSpeed, int newTotalVoltage,int newStateOfCharge,
			Map<String,Integer> newTemperatures, Map<Integer,ArrayList<Float>> newCellVoltages){*/
		TelemDataPacket newData = new TelemDataPacket(speed, totalVoltage, mapForTemperatures, mapForCellVoltages);
		this.lastLoadedDataPacket = newData;
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
	
	/**
	 * This class is made to be it's own thread so it can block waiting for a 
	 * new data packet from the car
	 */
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
			serialPort.closePort();
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

					System.out.println(this.lastLoadedDataPacket.toString());
				}else{
					serialReadBufPos++;
				}
			}
		} catch (SerialPortException e) {System.out.println(e);}
	}



}
