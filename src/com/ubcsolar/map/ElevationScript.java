package com.ubcsolar.map;

// im here

/*Google key: AIzaSyCMCYQ_X_BgCcGD43euexoiIJED__44mek
No IP address restrictions currently on, but if you're having trouble,
maybe check that. (maybe I dev'ed it at UBC, and my IP address is wrong
					now that I'm in the field?)
*/

import java.util.*;
import java.io.*;
import java.net.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class ElevationScript{
public static final String API_KEY = "AIzaSyCMCYQ_X_BgCcGD43euexoiIJED__44mek";
	static ArrayList<Coordinate> coordinateList = new ArrayList<Coordinate>();
	
	public static void main(String[] args) throws IOException{
	BufferedReader br = null;
	FileWriter fw = null;
	if(args.length<2){
		System.out.println("Usage: input file, output file");
		System.exit(1);
	}
	
	try{
		String sCurrentLine;
		br = new BufferedReader(new FileReader("res\\" + args[0]));
		int count = 0;
		fw = new FileWriter("res\\" + args[1]);
		boolean record = false;
		while((sCurrentLine = br.readLine()) != null){
			if(sCurrentLine.contains("<coordinates>")){
				//fw.write(sCurrentLine.replace(" ", " \n") + "\r\n");
				record = true;
				fw.write(sCurrentLine.replace(" ", " \n") + "\r\n");
			}
			else if(sCurrentLine.contains("</coordinates>")){
				record = false; 
				turnIntoElevations();
				printOutStuff(fw);
				
				fw.write(sCurrentLine + "\r\n");
			}
			else if(record){
				//System.out.println(sCurrentLine);
				parseAndAdd(sCurrentLine);
			}
			else{
				fw.write(sCurrentLine + "\r\n");
			}
			
			count ++;
		}
	}
	catch(IOException e){
		e.printStackTrace();
	}
	finally{
		try{
			if(br != null) br.close();
			if(fw != null) fw.close();
		}
			catch(IOException ex){
				ex.printStackTrace();
			}
		}
		System.out.println("Hello world");
	}
	public static void printOutStuff(FileWriter fw){
		
		for(int i=0; i<coordinateList.size(); i++){
			try{
				fw.write("" + coordinateList.get(i).getLat() + ',');
				fw.write("" + coordinateList.get(i).getLong() + ',');
				fw.write("" + coordinateList.get(i).getElevation() + "\r\n");
			}
			catch(IOException e){
				System.out.println("oops");
			}
		}
		coordinateList = new ArrayList<Coordinate>(); //replace it for the next section
	}
	
	/**
		note: returns in meters
	*/
	public static void turnIntoElevations() throws IOException{
	//http://maps.googleapis.com/maps/api/elevation/outputFormat?parameters
	//See documentation here: https://developers.google.com/maps/documentation/elevation/
	
	if(coordinateList.size()==0){
		return;
	}
	int maxCoordinatesInURL = 30; //Number of coordinates per URL. Each coord counts as a request. Not sure what 2000 characters looks like. 
	int remainder = coordinateList.size() % maxCoordinatesInURL;
	int numOfWholeParts = (coordinateList.size()-remainder)/maxCoordinatesInURL;
	
	System.out.println(coordinateList.size());
	
	System.out.println("parts: " + numOfWholeParts);
	for(int i = 0; i<numOfWholeParts; i++){
		turnPartIntoElevation(i*maxCoordinatesInURL, (i+1)*maxCoordinatesInURL);
	}
	turnPartIntoElevation(numOfWholeParts*maxCoordinatesInURL, coordinateList.size());
	
	
	
	}
	
	public static void turnPartIntoElevation(int start, int stopBefore) throws IOException{
		if(start >= stopBefore){
			System.out.println("start was after stop");
			return;
		}

		String urlToSend = "https://maps.googleapis.com/maps/api/elevation/json?locations=";
		for(int i=start; i<stopBefore-1; i++){
			urlToSend += "" + coordinateList.get(i).getLat() + "," + coordinateList.get(i).getLong() + "|";
		}
		urlToSend += "" + coordinateList.get(stopBefore-1).getLat() + "," + coordinateList.get(stopBefore-1).getLong();
		
		//TODO check for error messages to be nice to Google: 
		/*https://maps.googleapis.com/maps/api/elevation/json?locations=50.11133,-120.78621000000001&sensor=false&key=AIzaSyCMCYQ_X_BgCcGD43euexoiIJED__44mek
		{
		   "error_message" : "You have exceeded your daily request quota for this API.",
		   "results" : [],
		   "status" : "OVER_QUERY_LIMIT"
		}*/
		
		urlToSend += "&sensor=false&key=" + API_KEY;
		
		System.out.println(urlToSend);
		URL url = new URL(urlToSend); 
		InputStream inputStream = url.openStream(); 
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream)); 
		StringBuffer webPageData = new StringBuffer(); 
		//http://maps.googleapis.com/maps/api/elevation/json?locations=39.7391536,-104.9847034|36.455556,-116.866667&sensor=true_or_false&key=API_KEY
		
		String inputLine = null; 
		while ((inputLine = inputReader.readLine()) != null) { 
			webPageData.append(inputLine); 
			webPageData.append("\n"); 
		} 
		inputReader.close(); 
		System.out.println(webPageData.toString());
		JSONObject test = new JSONObject(webPageData.toString());
		JSONArray results = test.getJSONArray("results");
		//coordinateList = new ArrayList<Coordinate>();
		for(int i=0; i<results.length(); i++){
			coordinateList.get(start + i).setElevation(results.getJSONObject(i).getDouble("elevation"));
		}
		
		//System.out.println(results.getJSONObject(1).toString(0)); 
		
		}
	
	
	public static void parseAndAdd(String coordinateLine){
		//System.out.println(coordinateLine);
		//TODO: parse here
		String toAdd = coordinateLine.replace(" ", "").replace("	","");
		//String[] all coordinates = coordinateLine.split("[ ]+");  //For multiple coords on one line
		String[] bits = coordinateLine.split("[,]");
		
		if(bits.length!=3){
			System.out.println("nope");
			for(String b : bits){
				System.out.println(b);
			}
			return;
		}
		
		/*for(String b : bits){
				System.out.println(b);
			}*/
		//System.out.println(Double.parseDouble(bits[1]));
		
		/**NOTE: This won't work if we travel to somewhere that's not an E coordinate)!!*/
		Coordinate temp;
		if(Double.parseDouble(bits[0])<0){
		 temp = new Coordinate(Double.parseDouble(bits[1]), //NOTE: the KML data seems to be long, lat
										Double.parseDouble(bits[0]),
										Double.parseDouble(bits[2]));
		}
		else{
			temp = new Coordinate(Double.parseDouble(bits[1]), //NOTE: the KML data seems to be long, lat
					Double.parseDouble(bits[0]),
					Double.parseDouble(bits[2]));
		}
										
		//System.out.print(temp.getLong());
		coordinateList.add(temp);		
		
	//	coordinates.add(coordinateLine.replace(" ", "").replace("	",""));
		
	}
	
	private static class Coordinate{
		private double latitude;
		private double longitude;
		private double elevation;
		
		public Coordinate(double lat, double longitude, double elevation){
		this.latitude = lat;
		this.longitude = longitude;
		this.elevation = elevation;
		}
		
		public double getLat(){
		return latitude;
		}
		public double getLong(){
		return longitude;
		}
		public double getElevation(){
		return elevation;
		}
		public void setElevation(double newElevation){
		this.elevation = newElevation;
		}
	}
}