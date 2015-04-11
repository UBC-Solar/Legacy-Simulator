package com.ubcsolar.weather;

import com.github.dvdme.ForecastIOLib.FIOCurrently;
import com.github.dvdme.ForecastIOLib.ForecastIO;

public class FIOImplement {
	
	public String API_KEY = "49358bec9be8ea1393625a7334124152"; // API-KEY do not edit.
	public String Latitude ="49.2625160";// Enter Latitude here
	public String Longitude ="-123.2463750";// Enter Longitude here	
	private ForecastIO fio;
	private FIOCurrently currently;
	public static void main(String[] args){
		FIOImplement test = new FIOImplement();
		test.print();
	}
	public FIOImplement (){
	//this.fio = new ForecastIO(Latitude, Longitude, API_KEY);
	this.fio = new ForecastIO(API_KEY);
	fio.setUnits(ForecastIO.UNITS_SI);
	fio.setLang(ForecastIO.LANG_ENGLISH);
	fio.getForecast(Latitude, Longitude);
	}
	
	
	public void currentlyReport(){
		currently = new FIOCurrently(fio);
		} 
	//Can put this inside constructor, should I??
	
	public void print(){
		
		System.out.println("Latitude: " + fio.getLatitude());
		
		currentlyReport();
	    //Print currently data
		System.out.println("\nCurrently\n");
		String [] f  = currently.get().getFieldsArray();
		for(int i = 0; i<f.length;i++){
			System.out.println(f[i]+": "+currently.get().getByKey(f[i]));
		}
		
}
	
//TODO: Getter methods for the weather
//TODO: Should have a list for currently 
//TODO: Should have a list for daily 
	
//TODO: Should have a cloudCover, cloudCoverError, what other methods would be req'd?
	
//TODO: Data holder class: figure out the relationship between controller, acquire and the database
//TODO: Also notification for hey we have a new weather report
	
	
//TODO: Eventually this software should be source independent
//TODO: Maybe create a counter of all weather reports created, so that we don't go past our limit
	
	
	
	             
	
	             
	
	  


}
