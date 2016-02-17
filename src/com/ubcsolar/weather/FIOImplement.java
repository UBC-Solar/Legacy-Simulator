package com.ubcsolar.weather;

import com.github.dvdme.ForecastIOLib.FIOCurrently;
import com.github.dvdme.ForecastIOLib.ForecastIO;

public class FIOImplement {
	private final String API_KEY = "e4f99878991a762e85efbffc7db8d657"; //Strategy@ubcsolar.com's
	//public String API_KEY = "49358bec9be8ea1393625a7334124152"; // old but valid API-KEY.
	public String Latitude ="38.7252993";// Enter Latitude here
	public String Longitude ="-9.1500364";// Enter Longitude here	
	private ForecastIO fio;
	public static void main(String[] args){
		FIOImplement test = new FIOImplement();
		test.print();
	}
	public FIOImplement (){
	//this.fio = new ForecastIO(Latitude, Longitude, API_KEY);
	this.fio = new ForecastIO(API_KEY);
	fio.setUnits(ForecastIO.UNITS_SI);
	fio.setLang(ForecastIO.LANG_ENGLISH);
	fio.getForecast("38.7252993", "-9.1500364");
	}
	
	
	
	public void print(){
	
		System.out.println("Latitude: " + fio.getLatitude());
		
		FIOCurrently currently = new FIOCurrently(fio);
	    //Print currently data
		System.out.println("\nCurrently\n");
		String [] f  = currently.get().getFieldsArray();
		for(int i = 0; i<f.length;i++){
			System.out.println(f[i]+": "+currently.get().getByKey(f[i]));
		}
}
	
	//TODO: getter methods, for weather
	//TODO: data holder class: figure out the relationship between controller, acquire and the db
	//TODO: have it source independent
	//TODO: also notification for hey we have a new weather report 
	
	
	
	             
	
	             
	
	  


}
