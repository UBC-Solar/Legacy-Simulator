package com.ubcsolar.car;

import com.ubcsolar.common.ModuleController;
import com.ubcsolar.common.Notification;
import com.ubcsolar.ui.GlobalController;

public class CarController extends ModuleController {

	private DataReceiver myDataReceiver;
	public CarController(GlobalController toAdd) {
		super(toAdd);
		myDataReceiver = new DataReceiver(this);
		myDataReceiver.run();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void notify(Notification n) {
		this.mySession.notify(n);

	}

	@Override
	protected void register() {
		// TODO Auto-generated method stub

	}

}
