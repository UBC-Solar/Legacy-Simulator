package com.ubcsolar.map;
import java.io.*;
import java.net.URL;
import java.util.*;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.json.JSONArray;
import org.json.JSONObject;

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
	
	/**
	 * Parses the KML document and creates (and caches) the Route. 
	 * @param myDoc2
	 * @return
	 * @throws JDOMException
	 */
	private Route turnInToRoute(Document myDoc2) throws JDOMException {
		//Documentation: https://developers.google.com/kml/documentation/kmlreference
		//TODO test after downloading just the route and then test with downloading the entire map
		//(two different options on Google Maps) to make sure it works for both
		Element rootElement = myDoc2.getRootElement();
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
				GeoCoord location = parseSingleFromString(placeMarkToCheck.getChild("Point",theNameSpace).getChildText("coordinates",theNameSpace));
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
			//TODO add in support for the others. Check KML guide for all possible ones. 
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
				toReturn.add(parseSingleFromString(s));
			} catch (JDOMException e) {
				SolarLog.write(LogType.ERROR, System.currentTimeMillis(), "Rejected a coordinate while importing the KML; parsing error");
				e.printStackTrace();
			}
		}
		return toReturn;
	}

	/**
	 * Parses a single GeoCoord from a String. 
	 * @param childText
	 * @return
	 * @throws JDOMException - if it's not formatted right (i.e less than 3 parts).
	 */
	private GeoCoord parseSingleFromString(String childText) throws JDOMException {
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
		toReturn = new GeoCoord(Double.parseDouble(coordinatePieces[1]),
								Double.parseDouble(coordinatePieces[0]), //KML standard is lon, lat. Weird. 
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
	
	/**
	 * Recursively searches for and updates every coordinate note in the document under the Root Element. 
	 * Depth-first-search.
	 * @param rootElement
	 * @param maxCoordPerURL
	 * @throws IOException
	 */
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
	
	/**
	 * Turns a list of GeoCoords into a String in KML Format, separated by a space. 
	 * @param toPrintOut
	 * @return
	 */
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
		while((updated.size() < parsedTrack.size()) && (parsedTrack.size() - start)>1){
			if(end >= parsedTrack.size()){
				end = parsedTrack.size();
			}
			System.out.println("start: " + start);
			System.out.println("end: " + end);
			System.out.println("Size: " + parsedTrack.size());
			List<GeoCoord> toConvert = parsedTrack.subList(start, end);
			String urlToSend = makeGoogleURL(toConvert);
			System.out.println(urlToSend);
			String response = sendURL(urlToSend);
			System.out.println(response);
			updated.addAll(parseJSONResponse(response));
						
			start = end;
			end += maxCoordPerURL;
		}
		
		return updated;
	}
	

	
	/**
	 * Parses the response from Google Elevations Api and makes
	 * a list of coordinates. 
	 * @param JSONresponse
	 * @return
	 */
	private Collection<? extends GeoCoord> parseJSONResponse(String JSONresponse) {
		//TODO add check for API 'out of quota' response. 
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
		JSONObject test = new JSONObject(JSONresponse);
		JSONArray results = test.getJSONArray("results");
		ArrayList<GeoCoord> updatedPoints = new ArrayList<GeoCoord>(results.length());
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


	/**
	 * Connects to a URL, records response, then closes connection. (Good for REST APIs)
	 * @param urlToSend
	 * @return
	 * @throws IOException - if there was an error connecting, probably an Internet issue
	 */
	private String sendURL(String urlToSend) throws IOException {
		URL url = new URL(urlToSend); 
		InputStream inputStream = url.openStream(); 
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream)); 
		StringBuffer webPageData = new StringBuffer(); 		
		String inputLine = null; 
		while ((inputLine = inputReader.readLine()) != null) { 
			webPageData.append(inputLine); 
			webPageData.append("\n"); 
		} 
		inputReader.close();
		return webPageData.toString();
	}


	/**
	 * Generates the URL for Google's Elevations API. 
	 * @param coordsToConvert
	 * @return - the URL, ready to go. 
	 * @throws IllegalArgumentException - if the number of coordinates given exceed 1900 characters (max URL Length)
	 */
	private String makeGoogleURL(List<GeoCoord> coordsToConvert) throws IllegalArgumentException{
		String urlToSend = "https://maps.googleapis.com/maps/api/elevation/json?locations=";
		int maxInOneShot = 512;//max number of coords as per Google documentation. 
		for(int i = 0; i<(coordsToConvert.size()-1) && i<(maxInOneShot -1); i++){
				urlToSend += "" + coordsToConvert.get(i).getLat() + "," + coordsToConvert.get(i).getLon() + "|";
		}
		//to avoid adding the bar at the end. 
		urlToSend += "" + coordsToConvert.get(coordsToConvert.size()-1).getLat() + "," + coordsToConvert.get(coordsToConvert.size()-1).getLon();
		urlToSend += "&sensor=false&key=" + API_KEY;		
		if(urlToSend.length() > 1900){ //max URL length is 2,000 characters, but let's do 1900 to be safe.
			throw new IllegalArgumentException("URL too long, too many coords given");
		}
		return urlToSend;
		
		
	}

	public String getLoadedFileName() {
		return loadedFileName;
	}
	
	/**
	 * Write the entire KML document to file 
	 * @param fileWithAbsoluteFilename
	 * @throws IOException
	 */	
	public void saveToFile(String absoluteFileName) throws IOException{
		this.saveToFile(new File(absoluteFileName));
	}
	
	/**
	 * Write the entire KML document to file 
	 * @param fileWithAbsoluteFilename
	 * @throws IOException
	 */
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
