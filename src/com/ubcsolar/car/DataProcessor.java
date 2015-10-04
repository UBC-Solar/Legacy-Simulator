/**
 * This class essentially wraps a thread-safe list. It gets passed values (likely
 * from DataReceiver classes) and converts them if needed into the thread-safe list. 
 */

package com.ubcsolar.car;

import java.util.ArrayList;
import java.util.List;

public class DataProcessor {
//TODO make this implement Runnable. Want it to be a seperate thread than the one listening to inputs. 
	//private Database myDatabase;
	private CarController myController;
	private ArrayList<TelemDataPacket> list;
	
	public DataProcessor(CarController myController){
		//this.myDatabase = database;
		this.myController = myController;
	}
	
	/**
	 * Get the thread-safe list the datapackets are being stored into
	 * @return
	 */
	public ArrayList<TelemDataPacket> getList(){
		return list;
	}
	
	/**
	 * Easiest scenario; someone has done the work for you
	 * @param newDataPacket
	 */
	//NOTE: Overload this method if you want to pass it something else. 
	public void store(TelemDataPacket newDataPacket){
		list.add(newDataPacket);
	}
	
	

}
