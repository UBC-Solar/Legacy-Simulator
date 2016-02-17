/**
 * This class is meant to act a data receiver, but it 
 * programatically creates (fakes) receiving data packets.  
 * It's important to have this for testing; there will be all sorts
 * of scenarios that we want to test for tat we won't actually put the car into,
 * this will allow us to simulate those from the lowest level in this program. 
 */
package com.ubcsolar.car;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.ubcsolar.common.TelemDataPacket;

public class BasicSimulatedDataReceiver extends AbstractDataReceiver {

	private String name;
	private Timer autoGenerate;
	
	private double temps[] = new double[6];
	private double speed, totalV;
	
	
	public BasicSimulatedDataReceiver(DataProcessor myProcessor, CarController toAdd){
		super(toAdd, myProcessor); 
		name = "basic sim";
		for(int i=0; i<6; i++)
			temps[i] = 25.0;
		speed = 0;
		totalV = 44;
	}
	
	private void receiveNewPacket(TelemDataPacket newPacket){
		this.myDataProcessor.store(newPacket);
	}
		
	/**
	 * This class is Runnable (can be in it's own thread), this is what will be called to start it. 
	 */
	@Override
	public void run() {
		if(autoGenerate == null){
			createNewTimer();
		}
		else{ //we want to pretend we're connecting to a new car
			
			//UPDATE: if we want a new connection, we should make a new dataReceiver class, not handle it 
			//from within the same obect. 
			//autoGenerate.cancel(); 
			//createNewTimer();
		}
	}
	
	private void createNewTimer(){
		Long startDelay = (long) 0.0; //Start immediately 
		Long repetitionDelayInMS = (long) 1000; //once per second seems reasonable. 
												//will need to tune to match the actual speed of the car. 
		autoGenerate = new Timer("TheFakeCar");
		autoGenerate.schedule(new generateNewThings(), startDelay, repetitionDelayInMS);
	}
	
	//Need to kill all the threads here. 
	/**
	 * This method is required to implement Runnable. 
	 */
	public void stop(){
		autoGenerate.cancel();
		autoGenerate.cancel();
	}
	
	/**
	 * Every DataReceiver must have a name (in case we connect to multiple cars, or get old datapackets)
	 * ... Except that we kill and start a new car connection from within here. Maybe we should
	 * force the creation of a new SimDataReceiver. Instead of handling that within the class. 
	 */
	@Override 
	public String getName(){
		return name;
	}
	
	@Override
	void setName() {
		this.name = "fake!";
		
	}


	
/**
 * This private class is used to generate new data packets.
 * It has to be a timerTask because the parent class uses a Timer to 
 * regularly generate new data packets in it's own thread (just as this 
 * program will have no control over when it receives a packet from the car, it should
 * have no control over when it 'receives' a packet from this class)
 * @author Noah
 *
 */
private class generateNewThings extends TimerTask{
	int iterations = 0; //I put this in to calculate new values from. Simplistic algo
						//could (almost definitely) probably be improved.
	
	/**
	 * This method is what the timer calls. Calls the method in parent class to handle the new packet. 
	 *  (analogy is that the parent class 'receives' a new packet that we programmed instead of the car). 
	 */
	@Override
	public void run() {
		receiveNewPacket(generateNewTelemDataPack()); 
											
	}
	
	/**
	 * Here is where we should put whatever algorithm to simulate the car getting a data packet. 
	 * If we want really accurated simulated data, we should probably hook the sim in here,
	 * however, there are going to be cases where we want the results to be different than the simulator.
	 * so if anything, this class will have to run it's own sim rather than just connecting to the real one. 
	 * 
	 * One possibly easy solution is to just have this sequentially
	 * read a list of packets from a pre-programed list. 
	 * @return the next TelemDataPacket in the series. 
	 */
	private TelemDataPacket generateNewTelemDataPack(){
		Random rng = new Random();
		speed = speed + rng.nextFloat()*2 - 1;
		if(speed < 0)
			speed = 0;
		totalV = totalV + rng.nextFloat()*0.2 - 0.1;
		totalV = totalV > 50 ? 50 : totalV < 40 ? 40 : totalV;
		for(int i=0; i<6; i++)
			temps[i] += rng.nextFloat() - 0.5;
		
		HashMap<String,Integer> temperatures = new HashMap<String,Integer>();
		temperatures.put("bms", (int) temps[0]);
		temperatures.put("motor", (int) temps[1]);
		temperatures.put("pack0", (int) temps[2]);
		temperatures.put("pack1", (int) temps[3]);
		temperatures.put("pack2", (int) temps[4]);
		temperatures.put("pack3", (int) temps[5]);
		HashMap<Integer,ArrayList<Float>> cellVoltages = new HashMap<Integer,ArrayList<Float>>();
		for(int i = 0; i<4; i++){ //Current number of cells coming in pack is 4. Will probably have to adjust that.
			cellVoltages.put(i, generatePackVoltages(totalV));
		}
		TelemDataPacket tempPacket = new TelemDataPacket((int)speed, (int)totalV, temperatures, cellVoltages);
		iterations++;
		return tempPacket;
	}
	
	private ArrayList<Float> generatePackVoltages(double totalV){
		ArrayList<Float> cell1 = new ArrayList<Float>();
		Random rng = new Random();
		
		putPackVoltages(10, totalV, cell1);
		return cell1;
	}
	
	private void putPackVoltages(int nCells, double totalV, ArrayList<Float> voltages){
		if(nCells == 0){
			return;
		}else if(nCells == 1){
			voltages.add((float)totalV);
			return;
		}
		
		Random rng = new Random();
		int half1 = nCells / 2;
		double middleV = totalV * half1 / nCells;
		middleV = middleV * (1.0 + 0.2 * rng.nextFloat() + 0.1);
		putPackVoltages(half1, middleV, voltages);
		putPackVoltages(nCells-half1, totalV-middleV, voltages);
	}
}

}