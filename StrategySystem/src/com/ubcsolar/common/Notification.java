package com.ubcsolar.common;

public abstract class Notification {
	
	private long time;
	
	public Notification(){
		time = System.currentTimeMillis();
	}
	
	public long getTime(){
		return time;
	}

}
