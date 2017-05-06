package com.ubcsolar.Main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.ImageIcon;

import com.github.dvdme.ForecastIOLib.ForecastIO;

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
	
	//to convert from the ForecastIO default just using the time and not the date
	public final static SimpleDateFormat forecastIOTimeParser = new SimpleDateFormat("HH:mm:ss");
	
	//Strategy@ubcsolar.com's key
	public final static String WEATHER_KEY= "e4f99878991a762e85efbffc7db8d657";
	
	//hooman.vaseli@ubcsolar.com's key
//	public final static String WEATHER_KEY= "26b799961238234d81780b220bb1d7b5";
	
	public final static DateFormat dateFormatWithMillis = new SimpleDateFormat("HH:mm:ss.SSS"); //time format. ss = seconds, SSS = ms
	//couldn't manage to format milliseconds in a way that Excel can handle as time
	//so just generated a second column to be able to graph it properly.
	
	
	//If can connect to URL, internet connection is good. Else assume internet is down.
	//much more likely than Google being down...
	public final static String URL_TO_CHECK_INTERNET_WITH = "http://www.google.com";

	//what should be considered a timeout
	public static final int MAX_TIME_MS_WAIT_FOR_URL = 1000;
	
	public static final ImageIcon iconImage = new ImageIcon("res/windowIcon.png"); //the icon for the program
	
	
	public static final String DEFAULT_TILE_SAVE_LOCATION = System.getProperty("user.dir")+"/"+"res/tileCache/";
	
	/**
	 * The message shown on any 'advanced' window with a graph, to explain how to navigate
	 * the charts. 
	 */
	public static final String CHART_TUT_MESSAGE = "To navigate the plot: \n\n"
			+ "-zoom in/out with mouse wheel" +"\n\n"
			+ "-click and drage down-right to zoom in specific area" +"\n\n"
			+ "-CTRL+drag to move the plot" +"\n\n"
			+ "-click and drage up-left to reset the zoom" +"\n\n"
			+ "\n" + "ENJOY !"; //TODO
	
	/**
	 * Whether the user has hit 'don't show me again'
	 */
	public static boolean showChartNavigationTutorialAgain = true;
	
	public static final int OFFSET = -07;
	
	public static final String WEATHER_UNITS = ForecastIO.UNITS_CA;
	
	public static final String WEATHER_LANG = ForecastIO.LANG_ENGLISH;

	public static final double PANEL_EFFICIENCY = 0.1;
	//TODO: measure this somehow
	
	public static final double DRAG_COEFF = 0.7;
	//TODO: get someone else to measure this
	
	public static final double CAR_CROSS_SECTIONAL_AREA = 2.5;
	//TODO: get someone else to measure this

	public static final double CAR_MASS = 350;
	//roughly weight of car+driver+ballast? estimate (in kg), not actually measured yet
	
	public static final double KMH_TO_MS_FACTOR = 10.0/36.0;
	
	public static final double TIRE_PRESSURE = 1.7;
	//in bars, equivalent to ~35 psi
	
	public static final double ENGINE_EFF = 0.8;
	//TODO: determine a real value for this
	
	public static final double BATTERY_MAX_CHARGE = 20;
	//this value is in units of amp-hours

	public static final double MAX_SPEED = 55;
	//this value is in units of km/h, also needs to be verified with mech team

	public static final double MAX_ACCELERATION_MS2 = 1;
	//this value is in units of m/s^2

	public static final double MS2_TO_KMH2_FACTOR = 12960;

	public static final double MAX_ACCELERATION_KMH2 = MAX_ACCELERATION_MS2 * MS2_TO_KMH2_FACTOR;
}

