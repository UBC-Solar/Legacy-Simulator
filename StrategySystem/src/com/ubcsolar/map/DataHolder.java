/* Note: may be able to use Google's Directions API for instructions, a layover, and automatic getting. 
 * How to decode it vound here: http://stackoverflow.com/questions/9217274/how-to-decode-the-google-directions-api-polylines-field-into-lat-long-points-in
 */

package com.ubcsolar.map;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DataHolder {
	private MapController myMapController;
	private String filename;
	
	public DataHolder(String filename, MapController toAdd) throws IOException, SAXException, ParserConfigurationException{
		myMapController = toAdd;
		pureLoad(filename);
		this.filename = filename;
	}
	
	public String getFileName(){
		return filename;
	}
	
	
	private void pureLoad(String filename) throws IOException, SAXException, ParserConfigurationException{

		SaxKmlParser test = new SaxKmlParser();
		ArrayList<Point> allPoints = new ArrayList<Point>();
		test.parseToPoints(allPoints, filename);
	}
	
	
			//note: tag name is case sensitive. 
			private static String getValue(String tag, Element element) {
			NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
			Node node = (Node) nodes.item(0);
			return node.getNodeValue();
			}
	}
	
	
	
	
	
	

