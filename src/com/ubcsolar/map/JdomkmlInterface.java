package com.ubcsolar.map;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.PointOfInterest;
import com.ubcsolar.common.Route;
import com.ubcsolar.common.SolarLog;

public class JdomkmlInterface {

	private Document myDoc;
	private String loadedFileName;
	private Route cachedRoute;
	private final String API_KEY = "AIzaSyCMCYQ_X_BgCcGD43euexoiIJED__44mek";
	
	public JdomkmlInterface(String filename) throws IOException, JDOMException {
		dropCurrentAndLoad(filename);
	}
	
	
	/**
	 * Note that this won't save any changes that have been made. 
	 * @param filename
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public void dropCurrentAndLoad(String filename) throws IOException, JDOMException{
		//disconnect? 
		//TODO check for and drop extra stuff if it's an absolute file path. 
		loadedFileName = filename;
		try {
	         File inputFile = new File(filename);

	         SAXBuilder saxBuilder = new SAXBuilder();

	         myDoc = saxBuilder.build(inputFile);
	         
		}
		catch(IOException e){
			throw e;
		} catch (JDOMException e) {
			throw e;
		}
		
		this.cachedRoute = turnInToRoute(this.myDoc);
	}
	
	
	private Route turnInToRoute(Document myDoc2) throws JDOMException {
		//Documentation: https://developers.google.com/kml/documentation/kmlreference
		//TODO download just the route and then the entire map
		//(two different options on Google Maps) to make sure it works for both
		Element rootElement = myDoc2.getRootElement();
		Route toReturn = null;
		Element documentNode;
		Namespace theNameSpace = rootElement.getNamespace();
		if(!rootElement.getName().equalsIgnoreCase("kml")){
			throw new JDOMException("not a KML file");
		}
		if(rootElement.getName() == "Document"){
			documentNode = rootElement;
		}else{
			documentNode = rootElement.getChild("Document", theNameSpace);
		}
		
		String nameOfDocument = documentNode.getChildText("name",theNameSpace);
		List<Element> placemarks = documentNode.getChildren("Placemark",theNameSpace);
		ArrayList<GeoCoord> track = new ArrayList<GeoCoord>();
		ArrayList<PointOfInterest> pois = new ArrayList<PointOfInterest>();
		for(Element placeMarkToCheck : placemarks){
		//Note: element names are case-sensitive. 
			if(placeMarkToCheck.getChild("Point",theNameSpace) != null){
				String name = placeMarkToCheck.getChildText("name",theNameSpace);
				GeoCoord location = parseString(placeMarkToCheck.getChild("Point",theNameSpace).getChildText("coordinates",theNameSpace));
				String description = "";
				//TODO add in support for description (it's a 'cdata' tag, so I'm not sure)
				pois.add(new PointOfInterest(location, name, description));
			}
			if(placeMarkToCheck.getChild("LineString",theNameSpace) != null){
				ArrayList<GeoCoord> theTrack = parseTrack(placeMarkToCheck.getChild("LineString",theNameSpace).getChildText("coordinates",theNameSpace));
				if(theTrack != null){
				track.addAll(theTrack);
				}
			}
			//TODO add in support for the others. 
		}
		
		return new Route(nameOfDocument, track, pois);
		
	}


	private ArrayList<GeoCoord> parseTrack(String childText) {
		
		String[] roughCoordinates = childText.split("\\s");
		
		//Factor of half is a pretty good estimate/place to start. Minimize number of array
		//re-allocations.
		ArrayList<String> coordinates = new ArrayList<String>(roughCoordinates.length/2);
		
		for(String s : roughCoordinates){
			if(s.length()>5){ //(two commas, 3 numbers)
				coordinates.add(s);
			}
		}
		
		//it'll be the same size, might as well set that size before we start. 
		ArrayList<GeoCoord> toReturn = new ArrayList<GeoCoord>(coordinates.size());
		for(String s : coordinates){
			try {
				toReturn.add(parseString(s));
			} catch (JDOMException e) {
				SolarLog.write(LogType.ERROR, System.currentTimeMillis(), "Rejected a coordinate while importing the KML; parsing error");
				e.printStackTrace();
			}
		}
		return toReturn;
	}


	private GeoCoord parseString(String childText) throws JDOMException {
		childText = childText.replaceAll("\\s", ""); //to get rid of any tabs or spaces. 
		String[] coordinatePieces = childText.split("[,\\s]+");
		if(coordinatePieces.length<3){ //i.e not a valid coordinate. (even altitude 0 would be ok)
		for(String s : coordinatePieces){
			System.out.println(s);
		}
			throw new JDOMException("Not three parts to this coordinate. "
					+ "May have had whitespace or newlines between lat, long, and elevation");
			
		}
		GeoCoord toReturn;
		try{
			//TODO add a check to put Lon and Lat in right place. Google's KMLs seem to be backwards??
		toReturn = new GeoCoord(Double.parseDouble(coordinatePieces[1]),
								Double.parseDouble(coordinatePieces[0]), //Google's KML seems to be Lon, Lat??
								Double.parseDouble(coordinatePieces[2]));
		}
		catch(IllegalArgumentException e){ //if we can't parse into a Double
			throw new JDOMException("Error converting from String to coordinate."
					+ " Invalid Character?");
		}
		
		return toReturn;
	}


	public Route getRoute(){
		if(this.cachedRoute != null){
			return this.cachedRoute;
		}
		try {
			return turnInToRoute(this.myDoc);
		} catch (JDOMException e) {
			
			return null;
		}
	}
	
	
	/**
	 * Uses Google's Elevations API to get elevations for each coordinate
	 * @param resolution - how many Coordinates to send each URL request.
	 * Note that more coordinates reduces accuracy as per their API documentation
	 * Also note that we only have a fixed number of calls per 24 hour period.
	 * @throws JDOMException 
	 * @throws IOException 
	 */
	public Route getElevationsFromGoogle(int resolution) throws JDOMException, IOException{
		updateAllCoordinateNodes(this.myDoc.getRootElement(), resolution);
		this.cachedRoute = this.turnInToRoute(myDoc);
		return cachedRoute;
	}
	
	private void updateAllCoordinateNodes(Element rootElement, int maxCoordPerURL) throws IOException{
		if(rootElement.getName().equalsIgnoreCase("coordinates")){
			rootElement.setText(turnToString(parseAndUpdate(rootElement.getText(), maxCoordPerURL)));	
		}
		else{
			for(Element e : rootElement.getChildren()){
				updateAllCoordinateNodes(e, maxCoordPerURL);
			}
		}
		
	}
	
	
	private String turnToString(ArrayList<GeoCoord> toPrintOut) {
		//KML standard is Lon, Lat. Don't ask why...
		String toReturn = "";
		for(GeoCoord g : toPrintOut){
			toReturn += g.getLon();
			toReturn += ",";
			toReturn += g.getLat();
			toReturn += ",";
			toReturn += g.getElevation();
			toReturn += " ";
		}
		return toReturn;
	}


	private ArrayList<GeoCoord> parseAndUpdate(String text, int maxCoordPerURL) throws IOException {
		ArrayList<GeoCoord> parsedTrack = this.parseTrack((text));
		ArrayList<GeoCoord> updated = new ArrayList<GeoCoord>(parsedTrack.size());
		
		int start = 0;
		int end = maxCoordPerURL;
		while(updated.size() < parsedTrack.size()){
			if(end >= parsedTrack.size()){
				end = parsedTrack.size();
			}
			System.out.println("start: " + start);
			System.out.println("end: " + end);
			System.out.println("Size: " + parsedTrack.size());
			List toConvert = parsedTrack.subList(start, end);
			String urlToSend = makeURL(toConvert);
			System.out.println(urlToSend);
			String response = sendURL(urlToSend);
			updated.addAll(parseResponse(response, toConvert.size()));
			
			//TODO remove this. kluge to let it run
			//updated.addAll(parsedTrack.subList(start, end));
			
			start = end;
			end += maxCoordPerURL;
		}
		
		return updated;
	}
	

	private Collection<? extends GeoCoord> parseResponse(String JSONresponse, int numOfValues) {
		/*
		 *
	{
   "results" : [
      {
         "elevation" : 1107.862182617188,
         "location" : {
            "lat" : 51.0762327,
            "lng" : -114.1319691
         },
         "resolution" : 9.543951988220215
      }
   ],
   "status" : "OK"
}

		 */
		//If we know, might as well set to the proper size right away (instead of re-copying as it grows)
		ArrayList<GeoCoord> updatedPoints = new ArrayList<GeoCoord>(numOfValues);
		
		JSONObject test = new JSONObject(JSONresponse);
		JSONArray results = test.getJSONArray("results");
		//coordinateList = new ArrayList<Coordinate>();
		for(int i=0; i<results.length(); i++){
			JSONObject temp = results.getJSONObject(i);
			double elevation = temp.getDouble("elevation");
			double lat = temp.getJSONObject("location").getDouble("lat");
			double lon = temp.getJSONObject("location").getDouble("lng");
			updatedPoints.add(new GeoCoord(lat,lon,elevation));
		}
		
		return updatedPoints;
	}


	private String sendURL(String urlToSend) throws IOException {
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
		return webPageData.toString();
	}


	private String makeURL(List<GeoCoord> coordsToConvert) throws IllegalArgumentException{
		ArrayList<GeoCoord> updated = new ArrayList<GeoCoord>(coordsToConvert.size());
		String urlToSend = "https://maps.googleapis.com/maps/api/elevation/json?locations=";
		int maxInOneShot = 512;//max number of coords as per Google documentation. 
		for(int i = 0; i<(coordsToConvert.size()-1) && i<(maxInOneShot -1); i++){
				urlToSend += "" + coordsToConvert.get(i).getLat() + "," + coordsToConvert.get(i).getLon() + "|";
		}
		//to avoid adding the bar at the end. 
		urlToSend += "" + coordsToConvert.get(coordsToConvert.size()-1).getLat() + "," + coordsToConvert.get(coordsToConvert.size()-1).getLon();
		urlToSend += "&sensor=false&key=" + API_KEY;		
		if(urlToSend.length() > 1900){ //max is 2,000 characters, but lets do 1900 to be safe.
			throw new IllegalArgumentException("URL too long, too many coords given");
		}
		return urlToSend;
		
		
	}


	public void printToFile(String filename) throws IOException{
		//TODO: It look like Google's KML prints the coordinates backwards. When I save... do I want
		//to keep them in that order? 
		FileWriter fileToPrintTo = new FileWriter(new File(filename));
		fileToPrintTo.write(new XMLOutputter().outputString(this.myDoc));
		fileToPrintTo.close();
	}


	public String getLoadedFileName() {
		return loadedFileName;
	}
	
	public void saveToFile(String absoluteFileName) throws IOException{
		this.saveToFile(new File(absoluteFileName));
	}
	
	public void saveToFile(File fileWithAbsoluteFilename) throws IOException{
		FileWriter printer = null;
		try{
			printer = new FileWriter(fileWithAbsoluteFilename);
			printer.write(new XMLOutputter().outputString(this.myDoc));
		}
		catch(IOException e){
			throw e;
		}
		finally{
			if(printer != null){
				printer.close();
			}
		}
	}
	
	
	
}
