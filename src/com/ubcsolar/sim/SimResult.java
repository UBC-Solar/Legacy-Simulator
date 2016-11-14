package com.ubcsolar.sim;

import java.util.ArrayList;
import java.util.List;

import com.ubcsolar.common.SimFrame;
import com.ubcsolar.common.TelemDataPacket;

public class SimResult {
	private List<SimFrame> listOfFrames;
	private double travelTime;
	private TelemDataPacket endTelemData;
	
	public SimResult(List<SimFrame> frameList, double totalTime, TelemDataPacket endPacket){
		listOfFrames = frameList;
		travelTime = totalTime;
		endTelemData = endPacket;
	}
	
	public List<SimFrame> getListOfFrames(){
		return listOfFrames;
	}
	
	public double getTravelTime(){
		return travelTime;
	}
	
	public TelemDataPacket getFinalTelemData(){
		return endTelemData;
	}
	
//	/**
//	 * adds newFrame to the end of listOfFrames. With current implementation, frames must
//	 * be added sequentially (from frame corresponding to start of route to frame corresponding 
//	 * to end of route)
//	 * @param newFrame: the frame to be added
//	 */
//	public void addFrame(SimFrame newFrame){
//		listOfFrames.add(newFrame);
//	}
//	
//	/**
//	 * increases travelTime by nextTime
//	 * @param nextTime
//	 */
//	public void incrementTime(double nextTime){
//		travelTime += nextTime;
//	}
//	
//	public void setNewTelemData(TelemDataPacket newTelemData){
//		endTelemData = newTelemData;
//	}
	
}
