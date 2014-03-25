package com.ubcsolar.map;

import java.io.IOException;

import com.ubcsolar.common.ModuleController;
import com.ubcsolar.common.Notification;
import com.ubcsolar.ui.GlobalController;

public class MapController extends ModuleController{

	
	private DataHolder current;

	public MapController(GlobalController toAdd) {
		super(toAdd);
	}
	
	@Override
	protected void register(){
		//TODO register for any needed listeners. 
	}

	
	
	public void load(String filename) throws IOException{
		System.out.println("Loading " + filename);
		current = new DataHolder(filename, this);	
		sendNotification(new NewMapLoadedNotification(filename));
	}
	
	
	//TODO implement

	public String getLoadedMapName(){ 
		if(current == null){
			return null;
		}
		else{
			return current.getFileName();
		}
		
	}


	@Override
	public void notify(Notification n) {
		// TODO place to call things if something changes. 
		//don't imagine Map needs to know any notifications. 
		
	}

}
