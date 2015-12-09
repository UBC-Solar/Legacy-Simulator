package com.ubcsolar.map;
import java.io.*;
import java.util.*;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import com.ubcsolar.common.Route;

public class JdomkmlInterface {

	private Document myDoc;
	private String loadedFileName;
	
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
		this.loadedFileName = filename;
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
	}
	
	
	public Route getRoute(){
		return null;
	}
	
	
	/**
	 * Uses Google's Elevations API to get elevations for each coordinate
	 * @param resolution - how many Coordinates to send each URL request.
	 * Note that more coordinates reduces accuracy as per their API documentation
	 * Also note that we only have a fixed number of calls per 24 hour period.
	 */
	public void getElevationsFromGoogle(int resolution){
		
	}
	
	public void printToFile(String filename) throws IOException{
		FileWriter fileToPrintTo = new FileWriter(new File(filename));
		fileToPrintTo.write(new XMLOutputter().outputString(this.myDoc));
		fileToPrintTo.close();
	}
	
	
	
	
}
