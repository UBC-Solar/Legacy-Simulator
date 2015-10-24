package com.ubcsolar.database;

import com.ubcsolar.common.*;
import com.ubcsolar.common.ModuleController;
import com.ubcsolar.common.TelemDataPacket;
import com.ubcsolar.notification.NewDataUnitNotification;
import com.ubcsolar.notification.Notification;
import com.ubcsolar.ui.GlobalController;

public class DatabaseController extends ModuleController {

	public DatabaseController(GlobalController myGlobalController) {
		super(myGlobalController);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void notify(Notification n) {
		System.out.println("got notification!: ");
		if(n instanceof NewDataUnitNotification){
			
			store(((NewDataUnitNotification) n).getDataUnit());
		}

	}

	@Override
	public void register() {
		this.mySession.register(this, NewDataUnitNotification.class);

	}
	
	public void store(DataUnit toStore){
		if(toStore.getClass() == TelemDataPacket.class){
			System.out.println("GOT A DATA UNIT: " + toStore.getClass()); 
		}
		
	}

}
