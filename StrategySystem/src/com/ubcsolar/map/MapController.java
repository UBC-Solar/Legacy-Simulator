package com.ubcsolar.map;

public class MapController {
	DataHolder current;
	
	public void load(String filename){
	current = new DataHolder(filename);	
	}

}
