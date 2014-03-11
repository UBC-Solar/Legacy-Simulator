/* Note: may be able to use Google's Directions API for instructions, a layover, and automatic getting. 
 * How to decode it vound here: http://stackoverflow.com/questions/9217274/how-to-decode-the-google-directions-api-polylines-field-into-lat-long-points-in
 */

package com.ubcsolar.map;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DataHolder {

	public DataHolder(String filename) throws IOException{
		System.out.println("Map data holder has been created!");
		pureLoad(filename);
	}
	
	
	
	private void pureLoad(String filename) throws IOException{
		
		
		
		try {

			File xmlDoc = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlDoc);
			doc.getDocumentElement().normalize();

			System.out.println("root of xml file" + doc.getDocumentElement().getNodeName());
			NodeList nodes = doc.getElementsByTagName("Placemark");
			System.out.println("size: " + nodes.getLength());
			System.out.println("==========================");

			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				
				if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) node;
						NodeList subNodes = element.getElementsByTagName("LineString");
			
						//Node placeMarkOne = 
						for(int j=0; j<subNodes.getLength(); j++){
							Node subNode = subNodes.item(j);
							
							Element subElement = (Element) subNode;
							//NodeList subNodes2 = subElement.getElementsByTagName("coordinates");
							//System.out.println("Size: " + subNodes2.getLength());
							System.out.println(getValue("coordinates", subElement));
							System.out.println(subNode.getNodeName());
							
						}
						//System.out.println(node.getNodeName());
						/*System.out.println("Stock Symbol: " + getValue("Point", element));
				System.out.println("Stock Price: " + getValue("LineString", element));
				System.out.println("Stock Quantity: " + getValue("quantity", element));
						 */
				}
			}
			}catch(IOException e){
				throw e;
			}
			catch (Exception ex) {
				System.out.println("main2 failed");
			ex.printStackTrace();
			}
			}
	
	
			//note: tag name is case sensitive. 
			private static String getValue(String tag, Element element) {
			NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
			Node node = (Node) nodes.item(0);
			return node.getNodeValue();
			}
	}
	
	
	
	
	
	

