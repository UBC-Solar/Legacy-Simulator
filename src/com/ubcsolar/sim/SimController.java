/**
 * the interface for the UI and the sim. 
 * Can change settings and run new sims, and see the result of the past 
 * ones. 
 */

package com.ubcsolar.sim;
import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.ModuleController;
import com.ubcsolar.notification.Notification;

public class SimController extends ModuleController {

	public SimController(GlobalController toAdd) {
		super(toAdd);
	}

	
	/**
	 * this is where the class receives any notifications it registered for. 
	 * the "shoulder tap" 
	 */
	@Override
	public void notify(Notification n) {
		//handle any notifications that were registered for here

	}

	/**
	 * registers for any notifications it needs to hear
	 */
	@Override
	public void register() {
		// Do stuff here

	}
}
