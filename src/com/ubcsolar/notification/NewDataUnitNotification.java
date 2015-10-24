package com.ubcsolar.notification;

import com.ubcsolar.common.DataUnit;
import com.ubcsolar.notification.Notification;

public abstract class NewDataUnitNotification extends Notification {

	public abstract DataUnit getDataUnit();
	

}
