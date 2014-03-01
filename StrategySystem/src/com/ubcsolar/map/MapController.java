package com.ubcsolar.map;

public class MapController {
	static DataHolder current;
	
	public static void load(String filename){
	current = new DataHolder(filename);	
	}

}
