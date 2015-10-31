package com.ubcsolar.notification;

public class DatabaseDisconnectedOrClosed extends Notification {

	private final String databaseName;
	public DatabaseDisconnectedOrClosed(String name) {
		super();
		this.databaseName = name;
	}

	@Override
	public String getMessage() {
		return "database " + databaseName + " disconnected";
	}

	public String getName(){
		return databaseName;
	}
}



