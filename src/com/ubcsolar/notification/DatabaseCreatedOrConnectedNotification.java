package com.ubcsolar.notification;

public class DatabaseCreatedOrConnectedNotification extends Notification {

	private final String dbName;
	public DatabaseCreatedOrConnectedNotification(String dbName) {
		super();
		this.dbName = dbName;
	}

	@Override
	public String getMessage() {
		return "new DB created or connected: " + this.dbName;
	}
	
	public String getName(){
		return dbName;
	}

}
