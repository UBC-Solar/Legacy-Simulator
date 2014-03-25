package com.ubcsolar.common;

import com.ubcsolar.ui.GlobalController;

public abstract class ModuleController implements Listener {
	
	protected GlobalController mySession;
	public ModuleController(GlobalController toAdd){
		mySession = toAdd;
		register();
		
	}
	
	protected void sendNotification(Notification n){
		mySession.notify(n); 
	}
	
	protected abstract void register();
	
	

}
