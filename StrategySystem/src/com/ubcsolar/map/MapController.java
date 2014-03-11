package com.ubcsolar.map;

public class MapController {
	private static DataHolder current;
	
	public static void load(String filename){
		current = new DataHolder(filename);	
	}
	
	
	
	//TODO implement
	public static String getLoadedMapName(){
		if(current == null){
			return null;
		}
		else{
		return "hello world";
		}
		
	}

}
