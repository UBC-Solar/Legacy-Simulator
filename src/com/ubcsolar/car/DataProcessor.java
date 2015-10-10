/**
 * This class essentially wraps a thread-safe list. It gets passed values (likely
 * from DataReceiver classes) and converts them if needed into the thread-safe list. 
 */

package com.ubcsolar.car;

import java.util.ArrayList;
import java.util.List;

import com.ubcsolar.notification.CarUpdateNotification;

public class DataProcessor {
//TODO make this implement Runnable. Want it to be a seperate thread than the one listening to inputs. 
	//private Database myDatabase;
	private CarController myController;
	private ArrayList<TelemDataPacket> list;
	
	public DataProcessor(CarController myController){
		//this.myDatabase = database;
		this.myController = myController;
		list = new ArrayList<TelemDataPacket>();
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
	
	//NOTE: I would want this object to run in a different thread than Controller,
	//but not yet sure how to do it. One way is to store them in a mutually accesible list and 
	//control it with locks. I'm doing a notify method on the parent right now, but it might
	//not be the best method. 
	//'Storing' it by just adding it to the list without sending it anywhere might seem 
	//counter-intuitive, but this way it can run in it's own thread, and 
	//whatever other thread needs them can just check the list at their leisure. 
	public void store(TelemDataPacket newDataPacket){
		//list.add(newDataPacket); //use this for storing in list with locks. 
		myController.adviseOfNewCarReport(new CarUpdateNotification(newDataPacket));
		
	}
	
	

}
