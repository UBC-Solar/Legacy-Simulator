package com.ubcsolar.Main;

import java.text.SimpleDateFormat;

public class GlobalValues {
	
	//Hooman's key
	public final static String GOOGLE_MAPS_KEY = "AIzaSyDYE2CgPSZGLTJWMSaNSg4woYrZjJ_qwXk";
	
	//Strategy@ubcsolar.com's Key
//	public final static String GOOGLE_MAPS_KEY = "AIzaSyCMCYQ_X_BgCcGD43euexoiIJED__44mek";
	
	//Use for places where the time has to be displayed
	public final static SimpleDateFormat hourMinSec = new SimpleDateFormat("HH:mm:ss");
	
	//Use for places where the time has to be displayed
	public final static SimpleDateFormat dayMonthYr = new SimpleDateFormat("dd-MMM-yy");
	
	//to convert from the ForecastIO default. 
	public final static SimpleDateFormat forecastIODateParser = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	
	//Strategy@ubcsolar.com's key
	public final static String WEATHER_KEY= "e4f99878991a762e85efbffc7db8d657";

}
