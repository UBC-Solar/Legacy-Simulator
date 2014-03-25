/**
 * 
 */
package com.ubcsolar.common;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Noah
 *
 */
public class Log {
	private static Log oneInstance;
	
	private List<String> theLog;
	private List<LogType> theReasons;
	
	private Log(){
		theLog = new ArrayList<String>();
		theReasons = new ArrayList<LogType>();
	}
	
	
	private static Log getInstance(){
		if(oneInstance == null){
			oneInstance = new Log();
		}
		return oneInstance;
	}
	
	public void add(String message, LogType code){
		theLog.add(message);
		theReasons.add(code);
	}
	public static void printOut(){
		for(int i = 0; i<getInstance().theLog.size(); i++){
			System.out.println(getInstance().theReasons.get(i) + " | " 
								+ getInstance().theLog.get(i));
		}
	}
	
	public static void write(String message, LogType code){
		getInstance().add(message, code);
		
	}

}
