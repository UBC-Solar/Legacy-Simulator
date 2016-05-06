package com.ubcsolar.Main;

import java.io.IOException;

import com.ubcsolar.common.LogType;
import com.ubcsolar.common.SolarLog;

public class Main {
	@SuppressWarnings("unused")
	private static GlobalController theProgram;

	/**
	 * Launch the application.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException{
		SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(), "Application started");
		theProgram = new GlobalController(true);
	}

}
