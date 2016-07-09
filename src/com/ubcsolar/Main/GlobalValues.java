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
	
	public static final String WEATHER_UNITS = ForecastIO.UNITS_CA;
	
	public static final String WEATHER_LANG = ForecastIO.LANG_ENGLISH;
}

