/**
 * the interface for the UI and the sim. 
 * Can change settings and run new sims, and see the result of the past 
 * ones. 
 */

package com.ubcsolar.sim;
//TODO: implement
import com.ubcsolar.common.ModuleController;
import com.ubcsolar.common.Notification;
import com.ubcsolar.ui.GlobalController;

public class SimController extends ModuleController {

	public SimController(GlobalController toAdd) {
		super(toAdd);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * this is where the class receives any notifications it registered for. 
	 * the "shoulder tap" 
	 */
	@Override
	public void notify(Notification n) {
		//TODO handle any notifications that were registered for

	}

	/**
	 * registers for any notifications it needs to hear
	 */
	@Override
	protected void register() {
		// TODO Auto-generated method stub

	}
}
