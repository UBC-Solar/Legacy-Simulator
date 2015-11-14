package com.ubcsolar.Main;

import com.ubcsolar.common.LogType;
import com.ubcsolar.common.SolarLog;

public class Main {
	private static GlobalController theProgram;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(), "Application started");
		theProgram = new GlobalController();
	}

}
