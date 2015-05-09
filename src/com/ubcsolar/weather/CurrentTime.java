package com.ubcsolar.weather;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CurrentTime {
	
  public static void getCurrentTime() {
 	  
	  String currentTime;

	   DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	   //get current date time with Date()
	   Date date = new Date();	   
	   currentTime = dateFormat.format(date);
	   System.out.println(currentTime);

 
  }
}